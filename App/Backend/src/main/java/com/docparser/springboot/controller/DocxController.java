package com.docparser.springboot.controller;

import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.service.DocumentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin
@RestController
public class DocxController {
    @Autowired
    private DocumentParser documentParser;

    @GetMapping("/parseDocToChangeFontType")
    public ResponseEntity<Resource> changeFont(@RequestParam("filename") String fileName) throws IOException {
        // Code to save the file to a database or disk

        Resource resource = documentParser.changeFontType(fileName);

        HttpHeaders headers = new HttpHeaders();
        String modifiedFileName = documentParser.getUniqueFileName("FontTypeModifiedFile");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + modifiedFileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

    @GetMapping("/parseDocToIncreaseFontSize")
    public ResponseEntity<Resource> increaseFontSize(@RequestParam("filename") String fileName) throws IOException {
        // Code to save the file to a database or disk
        Resource resource = documentParser.increaseFont(fileName);
        HttpHeaders headers = new HttpHeaders();
        String modifiedFileName= documentParser.getUniqueFileName("FontSizeModifiedFile");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + modifiedFileName);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


     @GetMapping("/modifyFile")
    public ResponseEntity<S3StorageInfo> modifyDocument(@RequestParam("filename") String fileName) throws IOException  {
        // Code to save the file to a database or disk
        S3StorageInfo storageInfo = documentParser.modifyFile(fileName);
        return ResponseEntity.ok(storageInfo);
    }

    

}
