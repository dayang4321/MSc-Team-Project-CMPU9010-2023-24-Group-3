package com.docparser.springboot.service;

import com.docparser.springboot.model.S3StorageInfo;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.Random;

@Service
public class DocumentParser {
    private final S3BucketStorage s3FileUploadService;

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
            e.printStackTrace(); // Handle the exception
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
        File tempFile = File.createTempFile("modifiedFile", ".docx");
        new S3StorageInfo();
        S3StorageInfo storageInfo;
        tempFile.deleteOnExit();
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                paragraph.setAlignment(ParagraphAlignment.LEFT);
                CTSpacing ctSpacing = paragraph.getCTP().getPPr().isSetSpacing() ? paragraph.getCTP().getPPr().getSpacing() : paragraph.getCTP().getPPr().addNewSpacing();
                ctSpacing.setLineRule(STLineSpacingRule.AUTO);
                ctSpacing.setLine(new BigInteger(String.valueOf(360)));
                for (XWPFRun run : paragraph.getRuns()) {
                   run.setFontSize(16);
                   run.setFontFamily("Open Sans");
                    CTRPr rPr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
                    CTSignedTwipsMeasure charSpacing = rPr.addNewSpacing();
                    charSpacing.setVal(BigInteger.valueOf(50));
                }
            }
            FileOutputStream out = new FileOutputStream(tempFile);
            document.write(out);
            storageInfo = s3FileUploadService.uploadFile(tempFile);
            out.close();
        }
        return storageInfo;
    }

}