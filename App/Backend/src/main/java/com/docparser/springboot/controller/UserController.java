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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/user") // Maps HTTP requests to /api/user to methods in this controller
public class UserController {
    // Logger instance for logging information
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    // Spring's way of injecting the UserService bean into this class
    @Autowired
    private UserService userService;

    @GetMapping("/me") // Handles GET requests to /api/user/me
    public ResponseEntity<Object> getLoggedinUser(HttpServletRequest request) {
        // Retrieve the authorization token from the request header
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        Map<String, Object> object = new HashMap<>();
        // Use the token to get the currently logged-in user
        Optional<UserAccount> user = userService.getLoggedInUser(token.get().substring(7));
        // Add the user to the response if present
        object.put("user", user.isPresent() ? user.get() : null);
        return ResponseEntity.ok(object); // Return the response entity with user data
    }

    @GetMapping("/{id}") // Handles GET requests to /api/user/{id}
    public ResponseEntity<UserAccount> getUserById(@PathVariable String id) {
        // Fetch user by ID and return the user if present
        return ResponseEntity
                .ok(userService.fetchUserById(id).isPresent() ? userService.fetchUserById(id).get() : null);
    }

    @PostMapping("/presets") // Handles POST requests to /api/user/presets
    public ResponseEntity<Object> updateUserConfig(@RequestBody DocumentConfig documentConfig,
            HttpServletRequest request) {
        // Retrieve the authorization token from the request header
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        // Update user information with the provided document configuration
        userService.updateUserInfo(token.get().substring(7), documentConfig);
        return ResponseEntity.ok("success"); // Return success response
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> postFeedback(@RequestBody FeedBackForm feedBackForm, HttpServletRequest request)
            throws JsonProcessingException {
        // Code to save the file to a database or disk
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        // Save feedback information with the provided feedback form
        userService.saveFeedbackInfo(token.get().substring(7), feedBackForm);
        return ResponseEntity.ok("Feedback saved successfully"); // Return success response
    }
}
