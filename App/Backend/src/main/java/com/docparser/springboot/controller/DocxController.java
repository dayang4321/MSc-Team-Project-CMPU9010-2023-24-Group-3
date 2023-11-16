package com.docparser.springboot.controller;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.model.DocumentInfo;
import com.docparser.springboot.model.DocumentResponse;
import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.service.DocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@CrossOrigin
@RestController
public class DocxController {
    Logger logger = LoggerFactory.getLogger(DocxController.class);


    @Autowired
    private DocumentParser documentParser;


    @PostMapping("/uploadFile")
    public ResponseEntity<S3StorageInfo> fileUploading(@RequestParam("file") MultipartFile file) throws IOException {
        // Code to save the file to a database or disk
        S3StorageInfo storageInfo = documentParser.uploadFile(file);
        return ResponseEntity.ok(storageInfo);
    }


    @GetMapping("/modifyFile")
    public ResponseEntity<S3StorageInfo> modifyDocument(@RequestParam("filename") String fileName, @RequestParam("docID") String docID, @RequestParam("versionID") String versionID,
                                                        @RequestParam(required = false) String fontType,
                                                        @RequestParam(required = false) String fontSize,
                                                        @RequestParam(required = false) String fontColor,
                                                        @RequestParam(required = false) String lineSpacing,
                                                        @RequestParam(required = false) String characterSpacing,
                                                        @RequestParam(required = false) String backgroundColor,
                                                        @RequestParam(required = false) String alignment,
                                                        @RequestParam(required = false) Boolean generateTOC,
                                                        @RequestParam(required = false) Boolean removeItalics) throws IOException {
        // Code to save the file to a database or disk
        S3StorageInfo storageInfo = documentParser.modifyFile(fileName, docID, versionID, new DocumentConfig(fontType, fontSize, fontColor, backgroundColor, lineSpacing, characterSpacing,
                alignment, generateTOC, removeItalics));
        return ResponseEntity.ok(storageInfo);
    }


    @GetMapping("/versions")
    public ResponseEntity<Object> getDocumentVersions(@RequestParam("docID") String docID) {
        // Code to save the file to a database or disk
        return ResponseEntity.ok(documentParser.getDocumentVersions(docID));
    }

    @GetMapping("/document/{id}")
    public ResponseEntity<DocumentResponse> getDocumentInfo(@PathVariable String id) {
        // Code to save the file to a database or disk
        return ResponseEntity.ok(documentParser.fetchDocument(id));
    }

}
