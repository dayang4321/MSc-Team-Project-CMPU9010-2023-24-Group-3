package com.docparser.springboot.service;


import com.docparser.springboot.Repository.UserRepository;
import com.docparser.springboot.errorHandler.UserNotFoundException;
import com.docparser.springboot.model.*;
import com.docparser.springboot.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepository;


    public Optional<UserAccount> fetchUserByEmail(String email) {
        Optional<UserAccount> existingAccount = userRepository.getUserInfobyEmail(email);
        existingAccount.orElseThrow(() -> {
            logger.error("User not found");
            return new UserNotFoundException("User not found");
        });
        return existingAccount;
    }

    public Optional<UserAccount> fetchUserById(String id) {
        Optional<UserAccount> existingAccount = userRepository.getUserInfo(id);
        return existingAccount;
    }

    public void saveUser(UserAccount user) {
        userRepository.saveUser(user);
    }

    public Optional<UserResponse> getLoggedInUser(String token) {

        String updatedToken = token.substring(7);
        String userId = SessionUtils.getSessionIdFromToken(updatedToken);
        Optional<UserAccount> existingAccount = userRepository.getUserInfo(userId);
        if(existingAccount.isEmpty()){
           return Optional.empty();
        }
        Optional<UserResponse> userResponse = Optional.of(new UserResponse(existingAccount.get().getUserId(), existingAccount.get().getUsername(), existingAccount.get().getEmail(), existingAccount.get().getProvider()));
        return userResponse;
    }


}





