package com.docparser.springboot.controller;

import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.model.SessionInfo;
import com.docparser.springboot.service.DocumentParser;
import com.docparser.springboot.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
public class DocxController {
    Logger logger = LoggerFactory.getLogger(DocxController.class);
    @Autowired
    private ObjectMapper objectMapper;

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

    @PostMapping("/feedback")
    @ResponseBody
    public ResponseEntity<Object> feedback(@RequestBody FeedBackForm feedBackForm, HttpServletRequest request) throws JsonProcessingException {
        // Code to save the file to a database or disk
        Optional<String> token= Optional.of(request.getHeader("Authorization"));
        SessionInfo sessionInfo = sessionService.saveFeedbackInfo(token.get().substring(7), feedBackForm);
        logger.info("feedbackInfo fetched from DB "+objectMapper.writeValueAsString(sessionInfo.getFeedBackForms()));
        Map<String, Object> object = new HashMap<>();
        object.put("feedbackForms",sessionInfo.getFeedBackForms() );
        return ResponseEntity.ok(object);
    }


}
