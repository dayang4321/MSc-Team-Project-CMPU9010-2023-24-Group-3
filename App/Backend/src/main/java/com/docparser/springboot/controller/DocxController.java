package com.docparser.springboot.controller;

import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.service.S3BucketStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;




@RestController
public class DocxController {
    @Autowired
    private S3BucketStorage s3FileUploadService;

    @PostMapping("/uploadFile")
    public ResponseEntity<S3StorageInfo> fileUploading(@RequestParam("file") MultipartFile file)  {
        // Code to save the file to a database or disk
        S3StorageInfo storageInfo = s3FileUploadService.uploadFile(file);

        return ResponseEntity.ok(storageInfo);
    }



}
