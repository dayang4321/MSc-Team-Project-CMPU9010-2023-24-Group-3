package com.docparser.springboot.service;

import com.docparser.springboot.Repository.DocumentRepository;
import com.docparser.springboot.errorHandler.DocumentNotExist;
import com.docparser.springboot.errorHandler.DuplicateUpload;
import com.docparser.springboot.errorHandler.FileParsingException;
import com.docparser.springboot.model.*;
import com.docparser.springboot.utils.FileUtils;
import com.docparser.springboot.utils.SessionUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;
import java.time.Instant;
import java.util.*;


@Service
public class DocumentParser {
    Logger logger = LoggerFactory.getLogger(DocumentParser.class);
    private final S3BucketStorage s3FileUploadService;
    private final DocumentRepository documentRepository;
    private final DocumentProcessor documentProcessor;
    private final UserService userService;


    public DocumentParser(S3BucketStorage s3FileUploadService, DocumentRepository documentRepository, DocumentProcessor documentProcessor, UserService userService) {
        this.s3FileUploadService = s3FileUploadService;
        this.documentRepository = documentRepository;
        this.documentProcessor = documentProcessor;
        this.userService = userService;
    }

    private DocumentInfo checkStoredDocumentConfigs(String documentKey, String docID) {
        DocumentInfo documentInfo = documentRepository.getDocumentInfo(docID).orElseThrow(() -> new DocumentNotExist("Document does not exist"));
        if (!documentInfo.getDocumentKey().equals(documentKey)) {
            throw new DocumentNotExist("Document key does not match with the document ID");
        }
        return documentInfo;
    }


    private void modifyDocument(File tempFile, InputStream inputStream, DocumentConfig formattingConfig) {
        try {
            OPCPackage opcPackage = OPCPackage.open(inputStream);
            XWPFDocument document = new XWPFDocument(opcPackage);
            logger.info("modifying document " + tempFile.getName() + " with paragraphs : " + document.getParagraphs().size());
            XWPFDocument updated = documentProcessor.process(document, formattingConfig);
            FileOutputStream out = new FileOutputStream(tempFile);
            updated.write(out);
            out.close();
        } catch (Exception e) {
            logger.error("Error while modifying document" + e.getMessage());
            throw new FileParsingException("Error while modifying document" + e.getMessage());
        }
    }


    public DocumentResponse modifyFile(String key, String docID, String versionID, DocumentConfig formattingConfig, String token) throws IOException {
        String userId = SessionUtils.getSessionIdFromToken(token);
        userService.checkUserLoggedIn(userId);
        DocumentInfo documentInfo = checkStoredDocumentConfigs(key, docID);
        documentInfo.setDocumentConfig(formattingConfig);
        InputStream inputStream = s3FileUploadService.getFileStreamFromS3(key, versionID);
        File tempFile = new File(key);
        tempFile.deleteOnExit();
        logger.info("initiating file modification");
        modifyDocument(tempFile, inputStream, formattingConfig);
        logger.info("file modification completed");
        uploadFileAfterModification(tempFile, documentInfo);

        return fetchModifyResponse(documentInfo);
    }


    private List<VersionInfo> setDocumentVersions(PutObjectResponse s3response) {
        VersionInfo versionInfo = new VersionInfo();
        List<VersionInfo> documentVersions = new ArrayList<>();
        versionInfo.setETag(s3response.eTag());
        versionInfo.setVersionID(s3response.versionId());
        versionInfo.setCreatedDate(Instant.now());
        documentVersions.add(versionInfo);
        return documentVersions;
    }

    private void updateDocumentInfo(String docID, String documentKey, PutObjectResponse s3response) {
        DocumentInfo newDocumentInfo = new DocumentInfo();
        newDocumentInfo.setDocumentID(docID);
        newDocumentInfo.setDocumentKey(documentKey);
        newDocumentInfo.setCreatedDate(Instant.now());
        newDocumentInfo.setExpirationTime(Instant.now());
        newDocumentInfo.setDocumentVersions(setDocumentVersions(s3response));
        documentRepository.save(newDocumentInfo);
    }

    private void updateUserDocument(String userId, String docID, String documentKey) {
        Optional<UserAccount> userAccount = userService.fetchUserById(userId);
        if (userAccount.isPresent()) {
            List<UserDocument> userDocuments = userAccount.map(UserAccount::getUserDocuments).orElseGet(ArrayList::new);
            //  userDocuments.removeIf(userDocument -> userDocument.getExpirationTime().isAfter(Instant.now()));
            userDocuments.add(new UserDocument(docID, documentKey, Instant.now(), Instant.now().plusSeconds(24 * 60 * 60)));
            userAccount.get().setUserDocuments(userDocuments);
            userService.saveUser(userAccount.get());
        }
    }

    public S3StorageInfo uploadFile(MultipartFile multipartFile, String token) throws IOException {
        String userId = SessionUtils.getSessionIdFromToken(token);
        userService.checkUserLoggedIn(userId);
        File file = FileUtils.convertMultiPartToFile(multipartFile);
        String fileName = FileUtils.generateFileName(multipartFile);
        String docID = UUID.randomUUID().toString();
        updateUserDocument(userId, docID, fileName); // update user document

        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());

        updateDocumentInfo(docID, fileName, s3response);
        file.delete();
        return new S3StorageInfo(docID, fileUrl, fileName, s3response.versionId());
    }


    public void uploadFileAfterModification(File file, DocumentInfo documentInfo) throws IOException {
        String fileName = FileUtils.generateFileName(file);
        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        documentInfo.getDocumentVersions().add(new VersionInfo(s3response.versionId(), s3response.eTag(), Instant.now()));
        documentRepository.save(documentInfo);
        file.delete();

    }

    public HashMap<String, DocumentVersion> getDocumentVersions(DocumentInfo documentInfo) {
        Optional<VersionInfo> versionInfoOriginal = documentInfo.getDocumentVersions().stream().min(Comparator.comparing(VersionInfo::getCreatedDate));
        Optional<VersionInfo> versionInfoLatest = documentInfo.getDocumentVersions().stream().max(Comparator.comparing(VersionInfo::getCreatedDate));
        HashMap<String, DocumentVersion> versions = new HashMap<>();
        // Assuming s3FileUploadService.getUploadedObjectUrl returns the URL for the given version ID
        String originalUrl = s3FileUploadService.getUploadedObjectUrl(documentInfo.getDocumentKey(), versionInfoOriginal.get().getVersionID());
        String latestUrl = s3FileUploadService.getUploadedObjectUrl(documentInfo.getDocumentKey(), versionInfoLatest.get().getVersionID());

        versions.put("originalVersion", new DocumentVersion(originalUrl, versionInfoOriginal.get().getVersionID()));
        versions.put("currentVersion", new DocumentVersion(latestUrl, versionInfoLatest.get().getVersionID()));
        return versions;
    }

    private void setDocumentResponse(DocumentInfo documentInfo, DocumentResponse documentResponse) {
        documentResponse.setDocumentKey(documentInfo.getDocumentKey());
        documentResponse.setDocumentID(documentInfo.getDocumentID());
        documentResponse.setVersions(getDocumentVersions(documentInfo));
        documentResponse.setDocumentConfig(Optional.ofNullable(documentInfo.getDocumentConfig()).orElse(new DocumentConfig(null, null, null, null, null, null, null, null, null, null, null, null, null)));
    }

    public DocumentResponse fetchDocument(String docID, String token) {
        String userId = SessionUtils.getSessionIdFromToken(token);
        userService.checkUserLoggedIn(userId);
        Optional<DocumentInfo> documentInfo = documentRepository.getDocumentInfo(docID);
        DocumentResponse documentResponse = new DocumentResponse();
        documentInfo.ifPresent(info -> {
            setDocumentResponse(info, documentResponse);
        });
        return documentResponse;
    }

    public DocumentResponse fetchModifyResponse(DocumentInfo documentInfo) {
        DocumentResponse documentResponse = new DocumentResponse();
        setDocumentResponse(documentInfo, documentResponse);
        return documentResponse;
    }


    public void deleteStoredDocuments() {
        HashMap<String, List<String>> documentsList = documentRepository.getDocumentsExpired();
        logger.info("deleting documents ID's" + documentsList.get("documentIDs").toString());
        logger.info("deleting documents keys's" + documentsList.get("documentKeys").toString());
        if (!documentsList.isEmpty()) {
            s3FileUploadService.deleteBucketObjects(documentsList.get("documentKeys"));
            documentRepository.deleteDocument(documentsList.get("documentIDs"));
        }

    }
}