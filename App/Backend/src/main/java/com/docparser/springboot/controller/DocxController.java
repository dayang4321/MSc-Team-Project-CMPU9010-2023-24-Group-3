package com.docparser.springboot.controller;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.model.DocumentResponse;
import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.service.DocumentParser;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@CrossOrigin
@RestController
@RequestMapping("/api/file")
public class DocxController {
    Logger logger = LoggerFactory.getLogger(DocxController.class);


    @Autowired
    private DocumentParser documentParser;


    @PostMapping("/uploadFile")
    public ResponseEntity<S3StorageInfo> fileUploading(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        S3StorageInfo storageInfo = documentParser.uploadFile(file);
        return ResponseEntity.ok(storageInfo);
    }


    @PostMapping ("/modifyFile")
    public ResponseEntity<DocumentResponse> modifyDocument(@RequestParam("filename") String fileName, @RequestParam("docID") String docID, @RequestParam("versionID") String versionID,
                                                           @RequestBody DocumentConfig documentConfig) throws IOException {

        // Code to save the file to a database or disk
        DocumentResponse storageInfo = documentParser.modifyFile(fileName, docID, versionID, documentConfig);
        return ResponseEntity.ok(storageInfo);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentInfo(@PathVariable String id) {
        // Code to save the file to a database or disk
        return ResponseEntity.ok(documentParser.fetchDocument(id));
    }

}
