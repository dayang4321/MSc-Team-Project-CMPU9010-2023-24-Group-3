package com.docparser.springboot.controller;

import com.docparser.springboot.model.TokenResponse;
import com.docparser.springboot.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@CrossOrigin
@RestController
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private SessionService jwtTokenService;

    @GetMapping("/auth/token")
    public ResponseEntity<TokenResponse> authenticate() {
        return ResponseEntity.ok(jwtTokenService.generateAndSaveSessionInfo());
    }
}