package com.docparser.springboot.service;

import com.docparser.springboot.Repository.DocumentRepository;
import com.docparser.springboot.errorHandler.DocumentNotExist;
import com.docparser.springboot.errorHandler.FileParsingException;
import com.docparser.springboot.model.*;
import com.docparser.springboot.utils.FileUtils;
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
    private  final DocumentProcessor documentProcessor;


    public DocumentParser(S3BucketStorage s3FileUploadService, DocumentRepository documentRepository,DocumentProcessor documentProcessor) {
        this.s3FileUploadService = s3FileUploadService;
        this.documentRepository = documentRepository;
        this.documentProcessor = documentProcessor;
    }

    private void checkIfDBdocumentKeyExists(String documentKey, String docID) {
        documentRepository.getDocumentInfo(docID).ifPresent(documentInfo -> {
            if (!documentInfo.getDocumentKey().equals(documentKey)) {
                throw new DocumentNotExist("Document key does not match with the document ID");
            }
        });
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



    public DocumentResponse modifyFile(String key, String docID, String versionID, DocumentConfig formattingConfig) throws IOException {
        checkIfDBdocumentKeyExists(key, docID);
        InputStream inputStream = s3FileUploadService.getFileStreamFromS3(key, versionID);
        File tempFile = new File(key);
        tempFile.deleteOnExit();
        logger.info("initiating file modification");
        modifyDocument(tempFile, inputStream, formattingConfig);
        logger.info("file modification completed");
        uploadFileAfterModification(tempFile, docID, formattingConfig);

        return fetchDocument(docID);
    }


    private List<VersionInfo> setDocumentVersions(PutObjectResponse s3response, String fileName) {
        VersionInfo versionInfo = new VersionInfo();
        List<VersionInfo> documentVersions = new ArrayList<>();
        versionInfo.seteTag(s3response.eTag());
        versionInfo.setVersionID(s3response.versionId());
        versionInfo.setCreatedDate(Instant.now());
        documentVersions.add(versionInfo);
        return documentVersions;
    }


    public S3StorageInfo uploadFile(MultipartFile multipartFile) throws IOException {
        File file = FileUtils.convertMultiPartToFile(multipartFile);
        String fileName = FileUtils.generateFileName(multipartFile);
        DocumentInfo documentInfo = new DocumentInfo();
        //  List<ParagraphStyleInfo> paragraphStyleInfoList = fetchDocumentMetaData(file);
        documentInfo.setDocumentKey(fileName);

        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());
        documentInfo.setDocumentID(UUID.randomUUID().toString());

        documentInfo.setDocumentVersions(setDocumentVersions(s3response, fileName));
        documentRepository.save(documentInfo);
        file.delete();
        return new S3StorageInfo(documentInfo.getDocumentID(), fileUrl, fileName, s3response.versionId());
    }

    public void uploadFileAfterModification(File file, String docID, DocumentConfig formattingConfig) throws IOException {
        String fileName = FileUtils.generateFileName(file);
        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);

        Optional<DocumentInfo> documentInfo = documentRepository.getDocumentInfo(docID);
        documentInfo.ifPresent(info -> {
            info.getDocumentVersions().add(new VersionInfo(s3response.versionId(), s3response.eTag(), Instant.now()));
            info.setDocumentConfig(formattingConfig);
            documentRepository.save(info);
        });
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

    public DocumentResponse fetchDocument(String docID) {
        Optional<DocumentInfo> documentInfo = documentRepository.getDocumentInfo(docID);
        DocumentResponse documentResponse = new DocumentResponse();
        documentInfo.ifPresent(info -> {
            documentResponse.setDocumentKey(info.getDocumentKey());
            documentResponse.setDocumentID(info.getDocumentID());
            documentResponse.setVersions(getDocumentVersions(info));
            documentResponse.setDocumentConfig(
                    Optional.ofNullable(info.getDocumentConfig())
                            .orElse(new DocumentConfig(null, null, null, null, null, null, null, null, null,null,null)));
        });
        return documentResponse;
    }


}