package com.docparser.springboot.controller;

import com.docparser.springboot.model.Login;
import com.docparser.springboot.model.TokenResponse;
import com.docparser.springboot.service.SessionService;
import com.docparser.springboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@CrossOrigin
@RestController
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private SessionService jwtTokenService;
    @Autowired
    private UserService userService;

    @GetMapping("/auth/token")
    public ResponseEntity<TokenResponse> authenticate() {
        return ResponseEntity.ok(jwtTokenService.generateAndSaveSessionInfo());
    }
    @PostMapping("/auth/login")
    public ResponseEntity<Object> loginUserUsingLink(@RequestBody Login login) {
        userService.authenticateUserWithEmailLink(login.getEmail());
        return ResponseEntity.ok("Email sent successfully");
    }
    @GetMapping("/auth/validate")
    public ResponseEntity<TokenResponse> loginUserUsingLink(@RequestParam String token, @RequestParam String email) {
        return ResponseEntity.ok(userService.validateMagicToken(token, email));
    }
    @PostMapping("/auth/logout")
    public ResponseEntity<Object> logoutUser( HttpServletRequest request) {
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        userService.logoutUser(token.get().substring(7));
        return ResponseEntity.ok("User logged out successfully");
    }
}