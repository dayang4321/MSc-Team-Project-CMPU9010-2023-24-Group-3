package com.docparser.springboot.controller;

import com.docparser.springboot.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private SessionService jwtTokenService;

    @GetMapping("/getToken")
        public String authenticate(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String token = jwtTokenService.generateAndSaveUserInfo(ipAddress);
        logger.info("token generated for ip address "+ipAddress);
        return token;
    }
}
