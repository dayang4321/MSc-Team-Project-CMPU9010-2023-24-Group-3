package com.docparser.springboot.controller;

import com.docparser.springboot.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private SessionService jwtTokenService;

    @GetMapping("/getToken")
        public String authenticate(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String token = jwtTokenService.generateAndSaveUserInfo(ipAddress);
        return token;
    }
}
