package com.docparser.springboot.service;


import com.docparser.springboot.Repository.SessionRepository;
import com.docparser.springboot.Repository.UserRepository;
import com.docparser.springboot.errorHandler.SessionNotFoundException;
import com.docparser.springboot.errorHandler.UserNotFoundException;
import com.docparser.springboot.model.*;
import com.docparser.springboot.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final EmailService emailService;


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

    public Optional<UserAccount> getLoggedInUser(String token) {

        String updatedToken = token.substring(7);
        String userId = SessionUtils.getSessionIdFromToken(updatedToken);
        Optional<UserAccount> existingAccount = userRepository.getUserInfo(userId);
        if (existingAccount.isPresent()) {
            List<UserDocument> userDocuments = existingAccount.map(UserAccount::getUserDocuments).orElseGet(ArrayList::new);
            userDocuments.removeIf(userDoc ->
                    userDoc.getExpirationTime() != null && userDoc.getExpirationTime().isBefore(Instant.now()));
            existingAccount.get().setUserDocuments(userDocuments);
            saveUser(existingAccount.get());
        }
        return existingAccount;
    }

    public void authenticateUserWithEmailLink(String email) {
        SessionInfo sessionInfo = new SessionInfo();
        String sessionID = UUID.randomUUID().toString();
        sessionInfo.setSessionID(sessionID);
        sessionInfo.setCreatedDate(Instant.now());
        sessionInfo.setExpirationTime(Instant.now().plusSeconds(15 * 60));
        sessionRepository.save(sessionInfo);
        emailService.sendSimpleMessage(email, sessionID);
    }

    public TokenResponse validateMagicToken(String token, String email) {
        SessionInfo sessionInfo = sessionRepository.getSessionInfo(token);
        if (sessionInfo == null) {
            throw new SessionNotFoundException("Code not Valid");
        }
        if (sessionInfo.getExpirationTime().isBefore(Instant.now())) {
            throw new SessionNotFoundException("Code Expired");
        }
        Optional<UserAccount> userAccount = userRepository.getUserInfobyEmail(email);
        String userId = null;
        if (userAccount.isEmpty()) {
            userId = UUID.randomUUID().toString();
            UserAccount userAccount1 = new UserAccount();
            userAccount1.setEmail(email);
            userAccount1.setUserId(userId);
            userAccount1.setProvider("magicLink");
            userAccount1.setUsername(email);
            userAccount1.setUserPresets(new DocumentConfig("arial", "12", "000000", "FFFFFF", "1.5", "2.5", "LEFT", false, false, false, false));
            userRepository.saveUser(userAccount1);
        } else {
            userId = userAccount.get().getUserId();
        }
        String sessionID = SessionUtils.generateToken(userId, SessionUtils.getTime(), SessionUtils.getExpirationTime());
        return new TokenResponse(sessionID, SessionUtils.getExpirationTime().toInstant().toString());
    }

}





