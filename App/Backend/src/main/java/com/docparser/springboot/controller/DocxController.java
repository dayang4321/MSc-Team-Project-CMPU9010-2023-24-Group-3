package com.docparser.springboot.controller;

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
    public ResponseEntity<S3StorageInfo> fileUploading(@RequestParam("file") MultipartFile file) throws IOException  {
        // Code to save the file to a database or disk
        S3StorageInfo storageInfo = documentParser.uploadFile(file);
        return ResponseEntity.ok(storageInfo);
    }


    @GetMapping("/modifyFile")
    public ResponseEntity<S3StorageInfo> modifyDocument(@RequestParam("filename") String fileName,@RequestParam("docID") String docID) throws IOException {
        // Code to save the file to a database or disk
        S3StorageInfo storageInfo = documentParser.modifyFile(fileName,docID);
        return ResponseEntity.ok(storageInfo);
    }




}
