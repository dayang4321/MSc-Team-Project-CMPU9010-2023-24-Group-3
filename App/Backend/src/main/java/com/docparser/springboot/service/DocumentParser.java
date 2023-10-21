package com.docparser.springboot.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.Random;

@Service
public class DocumentParser {
    @Autowired
    private S3BucketStorage s3FileUploadService;

    public String getUnigueFileName(String appliedChange) {
        String uniqueFileName = appliedChange + System.currentTimeMillis() + "_" + new Random().nextInt(1000) + ".docx";
        return uniqueFileName;
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
                    if(fontSize != null && fontSize.getVal()!=null)
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

}