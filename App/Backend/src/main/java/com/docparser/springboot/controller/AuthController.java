package com.docparser.springboot.controller;

import com.docparser.springboot.model.Login;
import com.docparser.springboot.model.TokenResponse;
import com.docparser.springboot.service.SessionService;
import com.docparser.springboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class AuthController {


    private final SessionService jwtTokenService;
    private final UserService userService;

    // Endpoint to authenticate and generate a new token
    @GetMapping("/auth/token")
    public ResponseEntity<TokenResponse> authenticate() {
        // Generate and return the session info as a token response
        return ResponseEntity.ok(jwtTokenService.generateAndSaveSessionInfo());
    }

    // Endpoint to initiate login using an email link
    @PostMapping("/auth/login")
    public ResponseEntity<Object> loginUserUsingLink(@RequestBody Login login) {
        // Authenticate the user by sending an email link
        userService.authenticateUserWithEmailLink(login.getEmail());
        return ResponseEntity.ok("Email sent successfully");
    }

    // Endpoint to validate the login using a token received in the email
    @GetMapping("/auth/validate")
    public ResponseEntity<TokenResponse> loginUserUsingLink(@RequestParam String token, @RequestParam String email) {
        // Validate the magic token and return the response
        return ResponseEntity.ok(userService.validateMagicToken(token, email));
    }

    // Endpoint to log out the user
    @PostMapping("/auth/logout")
    public ResponseEntity<Object> logoutUser(HttpServletRequest request) {
        // Extract the token from the Authorization header
        Optional<String> token = Optional.of(request.getHeader("Authorization"));

        // Log the user out using the token
        userService.logoutUser(token.get().substring(7));
        return ResponseEntity.ok("User logged out successfully");
    }
}
