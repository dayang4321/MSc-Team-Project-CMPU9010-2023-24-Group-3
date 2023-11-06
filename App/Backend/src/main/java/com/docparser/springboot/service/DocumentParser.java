package com.docparser.springboot.service;

import com.docparser.springboot.Repository.DocumentRepository;
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

    public String getUniqueFileName(String appliedChange) {
       return appliedChange + System.currentTimeMillis() + "_" + new Random().nextInt(1000) + ".docx";
    }

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
           throw new IOException("Error while changing font type"+e.getMessage());
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

    public S3StorageInfo modifyFile(String key) throws IOException{
        InputStream inputStream = s3FileUploadService.getFileStreamFromS3(key);
       // File tempFile = new File("modified");
        File tempFile = new File(key);
        new S3StorageInfo();
        S3StorageInfo storageInfo;
        tempFile.deleteOnExit();
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            XWPFStyles f = document.getStyles();
            for (XWPFParagraph paragraph : document.getParagraphs()) {

                paragraph.setAlignment(ParagraphAlignment.LEFT);
                CTSpacing ctSpacing = paragraph.getCTP().getPPr().isSetSpacing() ? paragraph.getCTP().getPPr().getSpacing() : paragraph.getCTP().getPPr().addNewSpacing();
                ctSpacing.setLineRule(STLineSpacingRule.AUTO);
                ctSpacing.setLine(new BigInteger(String.valueOf(360)));
                for (XWPFRun run : paragraph.getRuns()) {
                   run.setFontSize(78);
                   run.setFontFamily("Leelawadee UI");
                    CTRPr rPr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
                    CTSignedTwipsMeasure charSpacing = rPr.addNewSpacing();
                    charSpacing.setVal(BigInteger.valueOf(50));
                }
            }
            FileOutputStream out = new FileOutputStream(tempFile);
            document.write(out);
            storageInfo = uploadFile(tempFile);
            out.close();
        }
        return storageInfo;
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
          throw new IOException("Error while fetching document metadata"+e.getMessage());
        }
        return  paragraphStyleInfoList;
    }

    public DocumentInfo uploadFile(MultipartFile multipartFile) throws IOException {
        File file = fileUtils.convertMultiPartToFile(multipartFile);
        String fileName = fileUtils.generateFileName(multipartFile);
        DocumentInfo documentInfo = new DocumentInfo();

        List<String> documentVersions = new ArrayList<>();

        List<ParagraphStyleInfo> paragraphStyleInfoList = fetchDocumentMetaData(file);

        documentInfo.setDocumentID(UUID.randomUUID().toString());
        documentInfo.setParagraphInfo(paragraphStyleInfoList);

        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        String fileUrl =s3FileUploadService.getUploadedObjectUrl(fileName, s3response.versionId());
        documentVersions.add(fileUrl);
        documentInfo.setDocumentVersions(documentVersions);
        documentRepository.save(documentInfo);

        file.delete();
        return documentInfo;
    }
    public S3StorageInfo uploadFile(File file) throws IOException {
        String fileName = fileUtils.generateFileName(file);
        PutObjectResponse s3response = s3FileUploadService.uploadFileToS3(fileName, file);
        String fileUrl = s3FileUploadService.getUploadedObjectUrl(fileName,s3response.versionId());
        file.delete(); // Delete the temporary file after successful upload
        return new S3StorageInfo(s3response.eTag(), fileUrl, fileName,s3response.versionId());
    }

}