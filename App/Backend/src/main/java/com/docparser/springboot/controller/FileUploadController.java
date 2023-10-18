package com.docparser.springboot.controller;


import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@RestController
public class FileUploadController {

    @GetMapping("/processDocx")
    public String processDocx() throws IOException {
        String fileName = "C:\\Group3\\example.docx";

        try (XWPFDocument doc = new XWPFDocument(
                Files.newInputStream(Paths.get(fileName)))) {

            XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc);
            String docText = xwpfWordExtractor.getText();

            // find number of words in the document
            long count = Arrays.stream(docText.split("\\s+")).count();

            return "Total words: " + count;
        }
    }
}
