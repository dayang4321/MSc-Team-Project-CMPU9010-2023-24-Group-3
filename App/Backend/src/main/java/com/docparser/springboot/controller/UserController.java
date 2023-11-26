package com.docparser.springboot.controller;

import com.docparser.springboot.model.UserAccount;
import com.docparser.springboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserAccount> getLoggedinUser(HttpServletRequest request) {
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        return ResponseEntity.ok(userService.getLoggedInUser(token.get()).get());
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserAccount> getUserById(String  id) {
        return ResponseEntity.ok(userService.fetchUserById(id).isPresent() ? userService.fetchUserById(id).get() : null);
    }
}
