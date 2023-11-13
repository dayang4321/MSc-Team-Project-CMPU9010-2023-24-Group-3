package com.docparser.springboot.service;

import com.docparser.springboot.Repository.DocumentRepository;
import com.docparser.springboot.errorHandler.FileParsingException;
import com.docparser.springboot.model.*;
import com.docparser.springboot.utils.FileUtils;
import com.docparser.springboot.utils.ParsingUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class DocumentParser {
    Logger logger = LoggerFactory.getLogger(DocumentParser.class);
    private final S3BucketStorage s3FileUploadService;

    private final FileUtils fileUtils;

    private final DocumentRepository documentRepository;



    public DocumentParser(S3BucketStorage s3FileUploadService, FileUtils fileUtils, DocumentRepository documentRepository) {

        this.s3FileUploadService = s3FileUploadService;
        this.fileUtils = fileUtils;
        this.documentRepository = documentRepository;

    }


    Function<String, Boolean> checkForFontParameterChange = fontParmeter -> fontParmeter != null && !fontParmeter.isEmpty();


    private final BiConsumer<XWPFRun, FormattingConfig> modifyRun =
            (run, formattingConfig) -> {
                if (checkForFontParameterChange.apply(formattingConfig.getFontSize()))
                    modifyLineFontSize(run, formattingConfig.getFontSize());
                if (checkForFontParameterChange.apply(formattingConfig.getFontColor()))
                    modifyLineFontColor(run, formattingConfig.getFontColor());
                if (checkForFontParameterChange.apply(formattingConfig.getFontType()))
                    modifyFontFamily(run, formattingConfig.getFontType());
                if (checkForFontParameterChange.apply(formattingConfig.getCharacterSpacing()))
                    modifyCharSpacing(run, formattingConfig.getCharacterSpacing());
            };

    private void modifyLineFontSize(XWPFRun run, String fontSize) {
        run.setFontSize(Integer.parseInt(fontSize));
    }

    private void modifyLineFontColor(XWPFRun run, String fontColor) {
        run.setColor(fontColor);
    }

    private void modifyFontFamily(XWPFRun run, String fontType) {
        run.setFontFamily(fontType);
    }

    private void modifyCharSpacing(XWPFRun run, String charSpacing) {
        CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        CTSignedTwipsMeasure charSpacingNew = rpr.addNewSpacing();
        charSpacingNew.setVal(ParsingUtils.mapStringToCharacterSpacingValueInBigInt(charSpacing));
    }

    private final BiConsumer<XWPFParagraph, FormattingConfig> modifyParagraph =
            (paragraph, formattingConfig) -> {
                if (checkForFontParameterChange.apply(formattingConfig.getAlignment()))
                    modifyAlignment(paragraph,formattingConfig.getAlignment());
                if (checkForFontParameterChange.apply(formattingConfig.getLineSpacing()))
                    modifyLineSpacing(paragraph, formattingConfig.getLineSpacing());
                if (checkForFontParameterChange.apply(formattingConfig.getBackgroundColor()))
                    modifyColorShading(paragraph, formattingConfig.getBackgroundColor());
                paragraph.getRuns().stream().forEach(run -> modifyRun.accept(run, formattingConfig));
            };


    private void modifyAlignment(XWPFParagraph paragraph, String alignment) {
        paragraph.setAlignment(ParsingUtils.mapStringToAlignment(alignment));
    }

    private void modifyLineSpacing(XWPFParagraph paragraph, String lineSpacing) {
        if (checkForFontParameterChange.apply(lineSpacing)) {
            CTPPr ctpPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
            if (ctpPr.isSetSpacing()) {
                ctpPr.getSpacing().setLineRule(STLineSpacingRule.AUTO);
                ctpPr.getSpacing().setLine(new BigInteger(lineSpacing));

            } else {
                ctpPr.addNewSpacing().setLineRule(STLineSpacingRule.AUTO);
                ctpPr.getSpacing().setLine(ParsingUtils.mapStringToLineSpacingValueInBigInt(lineSpacing));
            }
        }
    }

    private void modifyColorShading(XWPFParagraph paragraph, String colorShading) {
        CTPPr ctpPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
        Optional.ofNullable(ctpPr.getShd()).ifPresentOrElse(
                shd -> shd.setFill(colorShading),
                () -> ctpPr.addNewShd().setFill(colorShading)
        );
    }

    private void checkIfDBdocumentKeyExists(String documentKey, String docID) {
        Optional.of(documentRepository.getDocumentInfo(docID)).ifPresent(documentInfo -> {
            if (!documentInfo.getDocumentKey().equals(documentKey)) {
                throw new FileParsingException("Document key does not match with the document ID");
            }
        });
    }

    private void modifyDocument(File tempFile, InputStream inputStream, FormattingConfig formattingConfig) {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            logger.info("modifying document " + tempFile.getName() + " with paragraphs : " + document.getParagraphs().size());
            ParsingUtils.getParagraphsInTheDocument(document).stream().forEach(paragraph -> modifyParagraph.accept(paragraph, formattingConfig));
            FileOutputStream out = new FileOutputStream(tempFile);
            document.write(out);
            out.close();
        } catch (Exception e) {
            logger.error("Error while modifying document" + e.getMessage());
            throw new FileParsingException("Error while modifying document::" + e.getMessage()); // Handle or log the exception appropriately
        }
    }

    public S3StorageInfo modifyFile(String key, String docID, FormattingConfig formattingConfig) throws IOException {
        checkIfDBdocumentKeyExists(key, docID);
        InputStream inputStream = s3FileUploadService.getFileStreamFromS3(key);
        File tempFile = new File(key);
        tempFile.deleteOnExit();
        logger.info("initiating file modification");
        modifyDocument(tempFile, inputStream, formattingConfig);
        logger.info("file modification completed");
        return uploadFileAfterModification(tempFile, docID);
    }

    public List<ParagraphStyleInfo> fetchDocumentMetaData(File file) {
        List<ParagraphStyleInfo> paragraphStyleInfoList = new ArrayList<>();
        try (XWPFDocument document = new XWPFDocument(new FileInputStream(file))) {
            paragraphStyleInfoList = ParsingUtils.getParagraphsInTheDocument(document).stream()
                    .map(paragraph -> {
                        ParagraphStyleInfo paragraphStyleInfo = new ParagraphStyleInfo();
                        paragraphStyleInfo.setParagraphAlignment(paragraph.getAlignment().toString());
                        Optional<List<XWPFRun>> runs = Optional.ofNullable(paragraph.getRuns());
                        runs.ifPresent(r -> {
                            Optional<XWPFRun> run = r.stream().findFirst();
                            paragraphStyleInfo.setFontStyle(run.map(XWPFRun::getFontFamily).orElse(null));
                            paragraphStyleInfo.setFontSize(run.map(run1 -> String.valueOf(run1.getFontSize())).orElse(null));
                            paragraphStyleInfo.setFontColor(run.map(XWPFRun::getColor).orElse(null));
                        });
                        return paragraphStyleInfo;
                    })
                    .collect(Collectors.toList());
        } catch (IOException | RuntimeException e) {
            throw new FileParsingException("Error while fetching document metadata" + e.getMessage());
        }
        return paragraphStyleInfoList;
    }

    private List<VersionInfo> setDocumentVersions(PutObjectResponse s3response, String fileName) {
        VersionInfo versionInfo = new VersionInfo();
        List<VersionInfo> documentVersions = new ArrayList<>();
        versionInfo.seteTag(s3response.eTag());
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());
        versionInfo.setUrl(fileUrl);
        versionInfo.setVersionID(s3response.versionId());
        versionInfo.setCreatedDate(Instant.now());
        documentVersions.add(versionInfo);
        return documentVersions;
    }


    public S3StorageInfo uploadFile(MultipartFile multipartFile) throws IOException {
        File file = fileUtils.convertMultiPartToFile(multipartFile);
        String fileName = fileUtils.generateFileName(multipartFile);
        DocumentInfo documentInfo = new DocumentInfo();
        //  List<ParagraphStyleInfo> paragraphStyleInfoList = fetchDocumentMetaData(file);
        List<ParagraphStyleInfo> paragraphStyleInfoList = new ArrayList<>();
        documentInfo.setParagraphInfo(paragraphStyleInfoList);
        documentInfo.setDocumentKey(fileName);

        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());
        documentInfo.setDocumentID(UUID.randomUUID().toString());

        documentInfo.setDocumentVersions(setDocumentVersions(s3response, fileName));
        documentRepository.save(documentInfo);
        return new S3StorageInfo(documentInfo.getDocumentID(), fileUrl, fileName, s3response.versionId());
    }

    public S3StorageInfo uploadFileAfterModification(File file, String docID) throws IOException {
        String fileName = fileUtils.generateFileName(file);
        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());

        DocumentInfo documentInfo = documentRepository.getDocumentInfo(docID);
        documentInfo.getDocumentVersions().add(new VersionInfo(fileUrl, s3response.versionId(), s3response.eTag(), Instant.now()));
        documentRepository.save(documentInfo);
        return new S3StorageInfo(documentInfo.getDocumentID(), fileUrl, fileName, s3response.versionId());
    }



}