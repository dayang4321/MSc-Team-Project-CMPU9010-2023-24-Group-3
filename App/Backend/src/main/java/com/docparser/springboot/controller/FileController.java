package com.docparser.springboot.controller;

import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.service.S3BucketStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;



@RestController
public class FileController {
    @Autowired
    private S3BucketStorage s3FileUploadService;

    @PostMapping("/uploadFile")
    public ResponseEntity<S3StorageInfo> fileUploading(@RequestParam("file") MultipartFile file) throws IOException  {
        // Code to save the file to a database or disk
        S3StorageInfo storageInfo = s3FileUploadService.uploadFile(file);
        return ResponseEntity.ok(storageInfo);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> download(@RequestParam("filename") String fileName) throws IOException {
        // Code to save the file to a database or disk

        Resource resource =  s3FileUploadService.download(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=downloadedFile.docx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

}
