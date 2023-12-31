package com.docparser.springboot.controller;

import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback") // Sets the base URL for all methods in this controller
public class FeedbackController {
    // Logger instance for logging information
    Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    private final ObjectMapper objectMapper;
    private final SessionService sessionService;


    @PostMapping("/save")
    public ResponseEntity<String> postFeedback(@RequestBody FeedBackForm feedBackForm, HttpServletRequest request) {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader("Authorization"));

        sessionService.saveFeedbackInfo(token.get().substring(7), feedBackForm);
        return ResponseEntity.ok("Feedback saved successfully");
    }

    @GetMapping("/fetch") // Maps HTTP GET requests to /api/feedback/fetch
    public ResponseEntity<Object> getFeedback(HttpServletRequest request)  {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        List<FeedBackForm> feedBackForms = sessionService.getFeedBackForm(token.get().substring(7));
        logger.info("feedbackInfo fetched from DB:{} ", feedBackForms);

        // Prepares the response with the fetched data
        Map<String, Object> object = new HashMap<>();
        object.put("feedbackForms", feedBackForms);
        return ResponseEntity.ok(object);
    }

}
