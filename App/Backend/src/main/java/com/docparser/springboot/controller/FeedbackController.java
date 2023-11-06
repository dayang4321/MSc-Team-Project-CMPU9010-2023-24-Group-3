package com.docparser.springboot.controller;

import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@CrossOrigin
@RestController
public class FeedbackController {
    Logger logger = LoggerFactory.getLogger(FeedbackController.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SessionService sessionService;


    @ResponseBody
    @PostMapping("/postFeedback")
    public ResponseEntity<String> postFeedback(@RequestBody FeedBackForm feedBackForm, HttpServletRequest request) throws JsonProcessingException {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        sessionService.saveFeedbackInfo(token.get().substring(7), feedBackForm);

        return ResponseEntity.ok("Feedback saved successfully");
    }


    @GetMapping("/getFeedback")
    public ResponseEntity<Object> getFeedback(HttpServletRequest request) throws JsonProcessingException {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        List<FeedBackForm> feedBackForms = sessionService.getFeedBackForm(token.get().substring(7));
        logger.info("feedbackInfo fetched from DB " + objectMapper.writeValueAsString(feedBackForms));
        Map<String, Object> object = new HashMap<>();
        object.put("feedbackForms",feedBackForms);
        return ResponseEntity.ok(object);
    }

}
