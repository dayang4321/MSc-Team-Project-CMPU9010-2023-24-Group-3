package com.docparser.springboot.controller;

import com.docparser.springboot.model.*;
import com.docparser.springboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user") // Maps HTTP requests to /api/user to methods in this controller
public class UserController {


    private final UserService userService;
    private static final String AUTHORISATION = "Authorization";

    @GetMapping("/me")
    public ResponseEntity<Object> getLoggedinUser(HttpServletRequest request) {
        // Retrieve the authorization token from the request header
        Optional<String> token = Optional.of(request.getHeader(AUTHORISATION));
        Map<String, Object> object = new HashMap<>();
        // Use the token to get the currently logged-in user
        Optional<UserAccount> user = userService.getLoggedInUser(token.get().substring(7));
        // Add the user to the response if present
        object.put("user", user.orElse(null));
        return ResponseEntity.ok(object); // Return the response entity with user data
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccount> getUserById(@PathVariable String id) {
        // Fetch user by ID and return the user if present
        Optional<UserAccount> user = userService.fetchUserById(id);
        return ResponseEntity
                .ok(user.orElse(null)); // Return the response entity with user data
    }

    @PostMapping("/presets")
    public ResponseEntity<Object> updateUserConfig(@RequestBody DocumentConfig documentConfig,
                                                   HttpServletRequest request) {
        // Retrieve the authorization token from the request header
        Optional<String> token = Optional.of(request.getHeader(AUTHORISATION));
        // Update user information with the provided document configuration
        userService.updateUserInfo(token.get().substring(7), documentConfig);
        return ResponseEntity.ok("success"); // Return success response
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> postFeedback(@RequestBody FeedBackForm feedBackForm, HttpServletRequest request) {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader(AUTHORISATION));
        // Save feedback information with the provided feedback form
        userService.saveFeedbackInfo(token.get().substring(7), feedBackForm);
        return ResponseEntity.ok("Feedback saved successfully"); // Return success response
    }

    @GetMapping("/documents")
    public ResponseEntity<Object> getUserDocumentInfo( HttpServletRequest request) {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader(AUTHORISATION));
        // Save feedback information with the provided feedback form
        List<UserDocumentResponse> result = userService.getUserDocuments(token.get().substring(7));
        return ResponseEntity.ok(result); // Return success response
    }

    @DeleteMapping("/documents/{documentids}")
    public ResponseEntity<String> deleteUserDocuments(@PathVariable Set<String> documentids, HttpServletRequest request) {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader(AUTHORISATION));
        userService.deleteUserDocuments(token.get().substring(7), documentids);
        return ResponseEntity.ok("Documents deleted successfully");
    }
}
