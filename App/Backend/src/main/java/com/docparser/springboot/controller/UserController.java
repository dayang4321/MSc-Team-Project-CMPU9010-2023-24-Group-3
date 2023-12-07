package com.docparser.springboot.controller;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.UserAccount;
import com.docparser.springboot.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Object> getLoggedinUser(HttpServletRequest request) {
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        Map<String, Object> object = new HashMap<>();
        Optional<UserAccount> user = userService.getLoggedInUser(token.get().substring(7));
       object.put("user",user.isPresent() ? user.get() : null);
        return ResponseEntity.ok(object);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserAccount> getUserById(@PathVariable String  id) {
        return ResponseEntity.ok(userService.fetchUserById(id).isPresent() ? userService.fetchUserById(id).get() : null);
    }
    @PostMapping("/presets")
    public ResponseEntity<Object> updateUserConfig(@RequestBody DocumentConfig documentConfig, HttpServletRequest request) {
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        userService.updateUserInfo(token.get().substring(7),documentConfig);
        return  ResponseEntity.ok("success");
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> postFeedback(@RequestBody FeedBackForm feedBackForm, HttpServletRequest request) throws JsonProcessingException {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        userService.saveFeedbackInfo(token.get().substring(7), feedBackForm);
        return ResponseEntity.ok("Feedback saved successfully");
    }
    @DeleteMapping("/documents/{documentids}")
    public ResponseEntity<String> deleteUserDocuments( @PathVariable Set<String> documentids, HttpServletRequest request) throws JsonProcessingException {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        userService.deleteUserDocuments(token.get().substring(7),  documentids);
        return ResponseEntity.ok("Documents deleted successfully");
    }
}
