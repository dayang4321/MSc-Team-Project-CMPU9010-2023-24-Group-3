package com.docparser.springboot.controller;

import com.docparser.springboot.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin
@RestController
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private SessionService jwtTokenService;

    @GetMapping("/getToken")
    public String authenticate() {
        return jwtTokenService.generateAndSaveUserInfo();
    }
}