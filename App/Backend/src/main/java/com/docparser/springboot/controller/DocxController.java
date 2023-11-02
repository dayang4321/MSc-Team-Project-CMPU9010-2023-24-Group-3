package com.docparser.springboot.controller;

import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.model.SessionInfo;
import com.docparser.springboot.service.DocumentParser;
import com.docparser.springboot.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
public class DocxController {
    @Autowired
    private DocumentParser documentParser;
    @Autowired
    private SessionService sessionService;

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
        String modifiedFileName = documentParser.getUniqueFileName("FontSizeModifiedFile");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + modifiedFileName);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    @GetMapping("/modifyFile")
    public ResponseEntity<S3StorageInfo> modifyDocument(@RequestParam("filename") String fileName) throws IOException {
        // Code to save the file to a database or disk
        S3StorageInfo storageInfo = documentParser.modifyFile(fileName);
        return ResponseEntity.ok(storageInfo);
    }

    @GetMapping("/feedback")
    public ResponseEntity<SessionInfo> feedback(@RequestBody FeedBackForm feedBackForm, HttpServletRequest request) throws IOException {
        // Code to save the file to a database or disk
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        SessionInfo sessionInfo = sessionService.saveFeedbackInfo(token, feedBackForm);
        return ResponseEntity.ok(sessionInfo);
    }


}
