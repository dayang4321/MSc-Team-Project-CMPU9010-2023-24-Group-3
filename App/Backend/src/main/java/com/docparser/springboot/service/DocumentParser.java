package com.docparser.springboot.service;

import com.docparser.springboot.Repository.DocumentRepository;
import com.docparser.springboot.errorHandler.DocumentNotExist;
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
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;


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
    Function<Boolean, Boolean> checkForBooleanFontParameterChange = fontParmeter -> fontParmeter != null && fontParmeter;


    private final BiConsumer<XWPFRun, DocumentConfig> modifyRun = (run, formattingConfig) -> {
        if (checkForFontParameterChange.apply(formattingConfig.getFontSize()))
            modifyLineFontSize(run, formattingConfig.getFontSize());
        if (checkForFontParameterChange.apply(formattingConfig.getFontColor()))
            modifyLineFontColor(run, formattingConfig.getFontColor());
        if (checkForFontParameterChange.apply(formattingConfig.getFontType()))
            modifyFontFamily(run, formattingConfig.getFontType());
        if (checkForFontParameterChange.apply(formattingConfig.getCharacterSpacing()))
            modifyCharSpacing(run, formattingConfig.getCharacterSpacing());
        if (checkForBooleanFontParameterChange.apply(formattingConfig.getRemoveItalics()))
            modifyToRemoveItalics(run);
    };
    private final BiConsumer<XWPFRun, DocumentConfig> modifyHeadingRun = (run, formattingConfig) -> {
        run.setBold(true);
        if (checkForFontParameterChange.apply(formattingConfig.getFontSize()))
            modifyHeadingFontSize(run, formattingConfig.getFontSize());
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

    private void modifyHeadingFontSize(XWPFRun run, String fontSize) {
        run.setFontSize(ParsingUtils.getHeadingSize(Integer.parseInt(fontSize)));
    }

    private void modifyToRemoveItalics(XWPFRun run) {
        run.setItalic(false);
    }


    private void modifyLineFontColor(XWPFRun run, String fontColor) {
        run.setColor(fontColor);
    }

    private void modifyFontFamily(XWPFRun run, String fontType) {
        run.setFontFamily(ParsingUtils.mapStringToFontStyle(fontType));
    }

    private void modifyAlignment(XWPFParagraph paragraph, String alignment) {
        paragraph.setAlignment(ParsingUtils.mapStringToAlignment(alignment));
    }

    private void modifyCharSpacing(XWPFRun run, String charSpacing) {
        CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        CTSignedTwipsMeasure charSpacingNew = rpr.addNewSpacing();
        charSpacingNew.setVal(ParsingUtils.mapStringToCharacterSpacingValueInBigInt(charSpacing));
    }

    private final BiConsumer<XWPFParagraph, DocumentConfig> modifyParagraph = (paragraph, formattingConfig) -> {
        boolean headingFontSizeModified = false;
        if (checkForFontParameterChange.apply(formattingConfig.getAlignment()))
            modifyAlignment(paragraph, formattingConfig.getAlignment());
        if (checkForFontParameterChange.apply(formattingConfig.getLineSpacing()))
            modifyLineSpacing(paragraph, formattingConfig.getLineSpacing());
        if (checkForFontParameterChange.apply(formattingConfig.getBackgroundColor()))
            modifyColorShading(paragraph, formattingConfig.getBackgroundColor());
        if (checkForFontParameterChange.apply(formattingConfig.getFontSize()) && checkIfHeadingStylePresent(paragraph)) {
            modifyAlignment(paragraph, "CENTER");
            paragraph.getRuns().stream().findFirst().ifPresent(run -> modifyHeadingRun.accept(run, formattingConfig));
            headingFontSizeModified = true;
        }
        if (!headingFontSizeModified)
            paragraph.getRuns().stream().forEach(run -> modifyRun.accept(run, formattingConfig));
    };


    private boolean checkIfHeadingStylePresent(XWPFParagraph paragraph) {
        return paragraph.getStyleID() != null && paragraph.getStyleID().startsWith("Heading");
    }


    private void modifyHeading(String heading, XWPFParagraph paragraph) {
        XWPFRun run = ParsingUtils.createNewRun(paragraph);
        run.setText(heading);
        run.setFontFamily("Open Sans");
        run.setFontSize(16);
        run.setBold(true);
        run.addCarriageReturn();
    }

    private Optional<List<String>> extractHeadings(XWPFDocument document) {
        List<String> headings = new ArrayList<>();
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        paragraphs.stream().filter(this::checkIfHeadingStylePresent).forEach(paragraph -> headings.add(paragraph.getText()));
        return Optional.of(headings);
    }

    private XWPFParagraph createTableOfContents(List<String> headings, XWPFDocument document, DocumentConfig formattingConfig) {
        XWPFParagraph tocParagraph = ParsingUtils.createNewParagraph(document);
        tocParagraph.setPageBreak(true);
        tocParagraph.setAlignment(ParagraphAlignment.CENTER);
        tocParagraph.setStyle("Heading1");
        XWPFRun tocRun = tocParagraph.createRun();
        tocRun.setText("Table of Contents");
        tocRun.setFontFamily("Open Sans");
        tocRun.addBreak();
        tocRun.setFontSize(18);
        tocRun.setBold(true);
        headings.stream().forEach(heading -> modifyHeading(heading, tocParagraph));
        modifyParagraph.accept(tocParagraph, formattingConfig);
        return tocParagraph;
    }

    private void modifyLineSpacing(XWPFParagraph paragraph, String lineSpacing) {
        if (checkForFontParameterChange.apply(lineSpacing)) {
            CTPPr ctpPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
            if (ctpPr.isSetSpacing()) {
                ctpPr.getSpacing().setLineRule(STLineSpacingRule.AUTO);
                ctpPr.getSpacing().setLine(ParsingUtils.mapStringToLineSpacingValueInBigInt(lineSpacing));

            } else {
                ctpPr.addNewSpacing().setLineRule(STLineSpacingRule.AUTO);
                ctpPr.getSpacing().setLine(ParsingUtils.mapStringToLineSpacingValueInBigInt(lineSpacing));
            }
        }
    }

    private void modifyColorShading(XWPFParagraph paragraph, String colorShading) {
        CTPPr ctpPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
        Optional.ofNullable(ctpPr.getShd()).ifPresentOrElse(shd -> shd.setFill(colorShading), () -> ctpPr.addNewShd().setFill(colorShading));
    }

    private void checkIfDBdocumentKeyExists(String documentKey, String docID) {
        Optional.of(documentRepository.getDocumentInfo(docID)).ifPresent(documentInfo -> {
            if (!documentInfo.getDocumentKey().equals(documentKey)) {
                throw new DocumentNotExist("Document key does not match with the document ID");
            }
        });
    }


    private void modifyDocument(File tempFile, InputStream inputStream, DocumentConfig formattingConfig) {
        try {
            XWPFDocument document = new XWPFDocument(inputStream);
            logger.info("modifying document " + tempFile.getName() + " with paragraphs : " + document.getParagraphs().size());
            ParsingUtils.getParagraphsInTheDocument(document).stream().forEach(paragraph -> modifyParagraph.accept(paragraph, formattingConfig));
            if (checkForBooleanFontParameterChange.apply(formattingConfig.getGenerateTOC())) {
                modifyDocumentToc(document, formattingConfig);
            }
            FileOutputStream out = new FileOutputStream(tempFile);
            document.write(out);
            out.close();
        } catch (Exception e) {
            logger.error("Error while modifying document" + e.getMessage());
            throw new FileParsingException("Error while modifying document" + e.getMessage());
        }
    }

    private void modifyDocumentToc(XWPFDocument oldDocument, DocumentConfig formattingConfig) {
        Optional<List<String>> headings = extractHeadings(oldDocument);
        if (headings.isPresent()) {
            XWPFParagraph newParagraph = createTableOfContents(headings.get(), oldDocument, formattingConfig);
            // Move this paragraph to the beginning of the document
            CTBody body = oldDocument.getDocument().getBody();
            CTP newParagraphCtp = newParagraph.getCTP();
            body.insertNewP(0);
            CTP firstParagraph = body.getPArray(0);
            firstParagraph.set(newParagraphCtp);

// Remove the duplicate (the original new paragraph at the end)
            body.removeP(body.sizeOfPArray() - 1);
        }

    }

    public S3StorageInfo modifyFile(String key, String docID, String versionID, DocumentConfig formattingConfig) throws IOException {
        checkIfDBdocumentKeyExists(key, docID);
        InputStream inputStream = s3FileUploadService.getFileStreamFromS3(key, versionID);
        File tempFile = new File(key);
        tempFile.deleteOnExit();
        logger.info("initiating file modification");
        modifyDocument(tempFile, inputStream, formattingConfig);
        logger.info("file modification completed");
        return uploadFileAfterModification(tempFile, docID, formattingConfig);
    }


    private List<VersionInfo> setDocumentVersions(PutObjectResponse s3response, String fileName) {
        VersionInfo versionInfo = new VersionInfo();
        List<VersionInfo> documentVersions = new ArrayList<>();
        versionInfo.seteTag(s3response.eTag());
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());
        versionInfo.setUrl(fileUrl);
        versionInfo.setVersionID(s3response.versionId());
        documentVersions.add(versionInfo);
        return documentVersions;
    }


    public S3StorageInfo uploadFile(MultipartFile multipartFile) throws IOException {
        File file = fileUtils.convertMultiPartToFile(multipartFile);
        String fileName = fileUtils.generateFileName(multipartFile);
        DocumentInfo documentInfo = new DocumentInfo();
        //  List<ParagraphStyleInfo> paragraphStyleInfoList = fetchDocumentMetaData(file);
        documentInfo.setDocumentKey(fileName);

        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());
        documentInfo.setDocumentID(UUID.randomUUID().toString());

        documentInfo.setDocumentVersions(setDocumentVersions(s3response, fileName));
        documentRepository.save(documentInfo);
        return new S3StorageInfo(documentInfo.getDocumentID(), fileUrl, fileName, s3response.versionId());
    }

    public S3StorageInfo uploadFileAfterModification(File file, String docID, DocumentConfig formattingConfig) throws IOException {
        String fileName = fileUtils.generateFileName(file);
        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());

        DocumentInfo documentInfo = documentRepository.getDocumentInfo(docID);
        documentInfo.setDocumentConfig(formattingConfig);
        documentInfo.getDocumentVersions().add(new VersionInfo(fileUrl, s3response.versionId(), s3response.eTag(), Instant.now()));
        documentRepository.save(documentInfo);
        return new S3StorageInfo(documentInfo.getDocumentID(), fileUrl, fileName, s3response.versionId());
    }

    public HashMap<String, Object> getDocumentVersions(String docID) {
        DocumentInfo documentInfo = documentRepository.getDocumentInfo(docID);
        Optional<VersionInfo> versionInfoOriginal = documentInfo.getDocumentVersions().stream().min(Comparator.comparing(VersionInfo::getCreatedDate));
        Optional<VersionInfo> versionInfoLatest = documentInfo.getDocumentVersions().stream().max(Comparator.comparing(VersionInfo::getCreatedDate));
        HashMap<String, Object> versions = new HashMap<>();
        versions.put("originalVersion", versionInfoOriginal.get().getUrl());
        versions.put("latestVersion", versionInfoLatest.get().getUrl());
        return versions;
    }
    public DocumentInfo fetchDocument(String docID) {
        return documentRepository.getDocumentInfo(docID);
    }


}