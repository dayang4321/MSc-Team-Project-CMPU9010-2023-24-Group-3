package com.docparser.springboot.service;

import com.docparser.springboot.Repository.DocumentRepository;
import com.docparser.springboot.errorHandler.FileParsingException;
import com.docparser.springboot.model.DocumentInfo;
import com.docparser.springboot.model.ParagraphStyleInfo;
import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.utils.FileUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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
    private final S3BucketStorage s3FileUploadService;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private DocumentRepository documentRepository;

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


    public FileSystemResource changeFontType(String key) throws IOException {
        InputStream inputStream = s3FileUploadService.getFileStreamFromS3(key);
        File tempFile = File.createTempFile("modifiedFile", ".docx");
        tempFile.deleteOnExit();

        try (XWPFDocument document = new XWPFDocument(inputStream)) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {

                for (XWPFRun run : paragraph.getRuns()) {
                    CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
                    CTFonts fonts = rpr.isSetRFonts() ? rpr.getRFonts() : rpr.addNewRFonts();

                    // Set the font family to "Open Sans"
                    fonts.setAscii("Open Sans");
                    fonts.setHAnsi("Open Sans");
                    fonts.setCs("Open Sans");

                }
            }

            FileOutputStream out = new FileOutputStream(tempFile);
            document.write(out);
            out.close();
        } catch (IOException e) {
            throw new IOException("Error while changing font type" + e.getMessage());
        }

        return new FileSystemResource(tempFile);
    }

    public FileSystemResource increaseFont(String key) throws IOException {
        InputStream inputStream = s3FileUploadService.getFileStreamFromS3(key);
        File tempFile = File.createTempFile("modifiedFile", ".docx");
        tempFile.deleteOnExit();
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
                    CTHpsMeasure fontSize = rpr.isSetSz() ? rpr.getSz() : rpr.addNewSz();
                    if (fontSize != null && fontSize.getVal() != null)
                        fontSize.setVal(BigInteger.valueOf(fontSize.getVal().longValue() + 25));
                }
            }
            FileOutputStream out = new FileOutputStream(tempFile);
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception
        }
        return new FileSystemResource(tempFile);
    }



    private Consumer<XWPFRun> modifyRun() {
        return run -> {
            run.setFontSize(FONT_SIZE);
            CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
            Optional.ofNullable(rpr.getColor()).ifPresent(color -> color.setVal(FONT_COLOR));
            Optional.ofNullable(rpr.getColor()).ifPresentOrElse(
                    color -> {
                        color.setVal(FONT_COLOR);
                    },
                    () -> {
                        CTColor color = rpr.addNewColor();
                        color.setVal(FONT_COLOR);
                    }
            );
            run.setFontFamily(FONT_TYPE);
            run.setColor("FF0000");
            CTRPr rPr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
            CTSignedTwipsMeasure charSpacing = rPr.addNewSpacing();
            charSpacing.setVal(BigInteger.valueOf(CHAR_SPACING));
        };
    }

    private void modifyDocument(File tempFile, InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            document.getParagraphs().stream()
                    .forEach(paragraph -> {
                        paragraph.setAlignment(ParagraphAlignment.LEFT);
                        CTPPr ctpPr= paragraph.getCTP().getPPr();
                        if (ctpPr != null) {
                            if (ctpPr.isSetSpacing()) {
                                ctpPr.getSpacing().setLineRule(STLineSpacingRule.AUTO);
                                ctpPr.getSpacing().setLine(new BigInteger(String.valueOf((LINE_SPACING))));
                                Optional.ofNullable(ctpPr.getShd()).ifPresentOrElse(
                                        shd -> {
                                            shd.setFill(BACKGROUND_COLOR);
                                        },
                                        () -> {
                                            CTShd shd = ctpPr.addNewShd();
                                            shd.setFill(BACKGROUND_COLOR);
                                        }
                                );
                            } else {
                                ctpPr.addNewSpacing().setLineRule(STLineSpacingRule.AUTO);
                                ctpPr.getSpacing().setLine(new BigInteger(String.valueOf(LINE_SPACING)));
                            }
                        }
                        paragraph.getRuns().forEach(modifyRun());
                    });
            FileOutputStream out = new FileOutputStream(tempFile);
            document.write(out);
            out.close();
        } catch (Exception e) {
            throw new FileParsingException(e.getMessage()); // Handle or log the exception appropriately
        }
    }

    public S3StorageInfo modifyFile(String key, String docID) throws IOException {
        InputStream inputStream = s3FileUploadService.getFileStreamFromS3(key);
        File tempFile = new File(key);
        S3StorageInfo documentInfo = new S3StorageInfo();
        tempFile.deleteOnExit();
        modifyDocument(tempFile, inputStream);
        documentInfo = uploadFileAfterModification(tempFile, docID);

        return documentInfo;
    }

    public List<ParagraphStyleInfo> fetchDocumentMetaData(File file) throws IOException {
        List<ParagraphStyleInfo> paragraphStyleInfoList = new ArrayList<>();
        try (XWPFDocument document = new XWPFDocument(new FileInputStream(file))) {
            paragraphStyleInfoList = document.getParagraphs().stream()
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
        } catch (IOException e) {
            throw new IOException("Error while fetching document metadata" + e.getMessage());
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