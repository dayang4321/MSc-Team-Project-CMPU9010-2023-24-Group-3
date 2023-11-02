package com.docparser.springboot.controller;

import com.docparser.springboot.model.AuthRequest;
import com.docparser.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService jwtTokenService;

    @PostMapping("/authenticate")
        public String authenticate(@RequestBody AuthRequest request) {
        String ipAddress = request.getIpAddress();
        String token = jwtTokenService.generateAndSaveUserInfo(ipAddress);
        return token;
    }
}
