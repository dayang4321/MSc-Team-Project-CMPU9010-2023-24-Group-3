package com.docparser.springboot.controller;

import com.docparser.springboot.model.AccessTokenRequest;
import com.docparser.springboot.model.TokenResponse;
import com.docparser.springboot.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;


@CrossOrigin
@RestController
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private SessionService jwtTokenService;

    @GetMapping("/getToken")
    public ResponseEntity<TokenResponse> authenticate() {
        return ResponseEntity.ok(jwtTokenService.generateAndSaveSessionInfo());
    }
    @PostMapping("/google/token/verify")
    public ResponseEntity<Object> verifyGoogleAccessToken(@RequestBody AccessTokenRequest accessTokenRequest) throws GeneralSecurityException {
        return ResponseEntity.ok(jwtTokenService.verifyAndSaveGoogleUsers(accessTokenRequest));
    }
}