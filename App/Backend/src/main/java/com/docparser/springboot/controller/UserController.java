package com.docparser.springboot.controller;

import com.docparser.springboot.model.UserAccount;
import com.docparser.springboot.model.UserResponse;
import com.docparser.springboot.service.UserService;
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
@RequestMapping("/api/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Object> getLoggedinUser(HttpServletRequest request) {
        Optional<String> token = Optional.of(request.getHeader("Authorization"));
        Map<String, Object> object = new HashMap<>();
        Optional<UserResponse> user = userService.getLoggedInUser(token.get());
       object.put("user",user.isPresent() ? user.get() : null);
        return ResponseEntity.ok(object);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserAccount> getUserById(@PathVariable String  id) {
        return ResponseEntity.ok(userService.fetchUserById(id).isPresent() ? userService.fetchUserById(id).get() : null);
    }
}
