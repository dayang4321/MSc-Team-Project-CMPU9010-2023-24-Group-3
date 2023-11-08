package com.docparser.springboot.service;

import com.docparser.springboot.Repository.DocumentRepository;
import com.docparser.springboot.errorHandler.FileParsingException;
import com.docparser.springboot.model.DocumentInfo;
import com.docparser.springboot.model.ParagraphStyleInfo;
import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.utils.FileUtils;
import com.docparser.springboot.utils.ParsingUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Service
public class DocumentParser {
    Logger logger = LoggerFactory.getLogger(DocumentParser.class);
    private final S3BucketStorage s3FileUploadService;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ParsingUtils parsingUtils;


    public DocumentParser(S3BucketStorage s3FileUploadService) {
        this.s3FileUploadService = s3FileUploadService;
    }

    private static final String FONT_TYPE = "Open Sans";
    private static final int FONT_SIZE = 16;
    private static final String FONT_COLOR = "000000";
    private static final String BACKGROUND_COLOR = "FFFFFF";
    //private static final String PARAGRAPH_ALIGNMENT = "LEFT";
    private static final int LINE_SPACING = 360;
    private static final int CHAR_SPACING = 50;


    private Consumer<XWPFRun> modifyRun =
            run -> {
                modifyLineFontSize(run);
                modifyLineFontColor(run);
                modifyFontFamily(run);
                modifyCharSpacing(run);
            };
    private void modifyLineFontSize(XWPFRun run) {
       run.setFontSize(FONT_SIZE);
    }

    private void modifyLineFontColor(XWPFRun run) {
        run.setColor(FONT_COLOR);
    }

    private void modifyFontFamily(XWPFRun run) {
        run.setFontFamily(FONT_TYPE);
    }

    private void modifyCharSpacing(XWPFRun run) {
        CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        CTSignedTwipsMeasure charSpacing = rpr.addNewSpacing();
        charSpacing.setVal(BigInteger.valueOf(CHAR_SPACING));
    }

    private Consumer<XWPFParagraph> modifyParagraph =
            paragraph -> {
                modifyAlignment(paragraph);
                modifyLineSpacing(paragraph);
                modifyColorShading(paragraph);
                paragraph.getRuns().stream().forEach(modifyRun);
            };



    private void modifyAlignment(XWPFParagraph paragraph) {
        paragraph.setAlignment(ParagraphAlignment.LEFT);
    }

    private void modifyLineSpacing(XWPFParagraph paragraph) {
        CTPPr ctpPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
        if (ctpPr.isSetSpacing()) {
            ctpPr.getSpacing().setLineRule(STLineSpacingRule.AUTO);
            ctpPr.getSpacing().setLine(new BigInteger(String.valueOf((LINE_SPACING))));

        } else {
            ctpPr.addNewSpacing().setLineRule(STLineSpacingRule.AUTO);
            ctpPr.getSpacing().setLine(new BigInteger(String.valueOf(LINE_SPACING)));
        }
    }

    private void modifyColorShading(XWPFParagraph paragraph) {
        CTPPr ctpPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
        Optional.ofNullable(ctpPr.getShd()).ifPresentOrElse(
                shd -> shd.setFill(BACKGROUND_COLOR),
                () -> {
                    ctpPr.addNewShd().setFill(BACKGROUND_COLOR);
                }
        );
    }

    private void modifyDocument(File tempFile, InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            logger.info("modifying document " + tempFile.getName() + " with paragraphs : " + document.getParagraphs().size());
            parsingUtils.getParagraphsInTheDocument(document).stream().forEach(modifyParagraph);
            FileOutputStream out = new FileOutputStream(tempFile);
            document.write(out);
            out.close();
        } catch (Exception e) {
            logger.error("Error while modifying document" + e.getMessage());
            throw new FileParsingException(e.getMessage()); // Handle or log the exception appropriately
        }
    }

    public S3StorageInfo modifyFile(String key, String docID) throws IOException {
        InputStream inputStream = s3FileUploadService.getFileStreamFromS3(key);
        File tempFile = new File(key);
        tempFile.deleteOnExit();
        logger.info("initiating file modification");
        modifyDocument(tempFile, inputStream);
        logger.info("file modification completed");
        return uploadFileAfterModification(tempFile, docID);
    }

    public List<ParagraphStyleInfo> fetchDocumentMetaData(File file) throws IOException {
        List<ParagraphStyleInfo> paragraphStyleInfoList = new ArrayList<>();
        try (XWPFDocument document = new XWPFDocument(new FileInputStream(file))) {
            parsingUtils.getParagraphsInTheDocument(document).stream()
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
        } catch (IOException  | RuntimeException e) {
            throw new FileParsingException("Error while fetching document metadata" + e.getMessage());
        }
        return paragraphStyleInfoList;
    }

    public S3StorageInfo uploadFile(MultipartFile multipartFile) throws IOException {
        File file = fileUtils.convertMultiPartToFile(multipartFile);
        String fileName = fileUtils.generateFileName(multipartFile);
        DocumentInfo documentInfo = new DocumentInfo();
        List<String> documentVersions = new ArrayList<>();

        List<ParagraphStyleInfo> paragraphStyleInfoList = fetchDocumentMetaData(file);
        documentInfo.setParagraphInfo(paragraphStyleInfoList);
        documentInfo.setDocumentKey(fileName);

        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        documentInfo.setDocumentID(UUID.randomUUID().toString());
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());
        documentVersions.add(fileUrl);
        documentInfo.setDocumentVersions(documentVersions);
        documentRepository.save(documentInfo);

        file.delete();
        return new S3StorageInfo(documentInfo.getDocumentID(), fileUrl, fileName, s3response.versionId());
    }

    public S3StorageInfo uploadFileAfterModification(File file, String docID) throws IOException {
        String fileName = fileUtils.generateFileName(file);
        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());

        DocumentInfo documentInfo = documentRepository.getDocumentInfo(docID);
        documentInfo.getDocumentVersions().add(fileUrl);
        documentRepository.save(documentInfo);

        file.delete(); // Delete the temporary file after successful upload
        return new S3StorageInfo(documentInfo.getDocumentID(), fileUrl, fileName, s3response.versionId());
    }

}