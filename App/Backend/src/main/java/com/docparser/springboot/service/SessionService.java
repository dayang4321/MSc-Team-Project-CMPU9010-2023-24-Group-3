package com.docparser.springboot.service;

import com.docparser.springboot.Repository.SessionRepository;
import com.docparser.springboot.Repository.UserRepository;
import com.docparser.springboot.errorHandler.GoogleSecurityException;
import com.docparser.springboot.errorHandler.SessionNotFoundException;
import com.docparser.springboot.model.*;
import com.docparser.springboot.utils.SessionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.*;
import org.apache.catalina.User;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.*;

@Service
public class SessionService {
    Logger logger = LoggerFactory.getLogger(SessionService.class);
    @Value("${google.clientId}")
    private String clientId;

    private  final ObjectMapper objectMapper;

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final  GoogleIdTokenVerifier verifier;

    public SessionService(ObjectMapper objectMapper, SessionRepository sessionRepository,UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.userRepository= userRepository;
        this.sessionRepository = sessionRepository;
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory =  GsonFactory.getDefaultInstance();
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

public TokenResponse saveSessionInfo(String id) {
    Instant expirationTime = Instant.now().plusSeconds(24 * 60 * 60);
    Instant issuedAt = Instant.now();
    String token = SessionUtils.generateToken(id, Date.from(issuedAt),Date.from(expirationTime));
    sessionRepository.save(new SessionInfo(id, token, Instant.now()));
    logger.info("token generated  and saved in DB" + token);
    return new TokenResponse(token, expirationTime.toString());
}
    public TokenResponse generateAndSaveSessionInfo() {
        String sessionID = UUID.randomUUID().toString();
       return saveSessionInfo(sessionID);
    }
    public UserAccount createOrUpdateUser(UserAccount account) {
        UserAccount existingAccount = userRepository.getUserInfobyEmail(account.getEmail());
        if (existingAccount == null) {
            userRepository.saveUser(account);
            return account;
        }
        existingAccount.setFirstName(account.getFirstName());
        existingAccount.setLastName(account.getLastName());
        userRepository.saveUser(existingAccount);
        return existingAccount;
    }
    public TokenResponse verifyAndSaveGoogleUsers(AccessTokenRequest accessTokenRequest) throws GeneralSecurityException {
        try {
            GoogleIdToken idTokenObj = GoogleIdToken.parse(verifier.getJsonFactory(), accessTokenRequest.getIdToken());
            if (idTokenObj == null) {
               throw new GoogleSecurityException("Invalid token");
            }
            JSONObject obj =SessionUtils.getUserInfoFromGoogleOauthApi(accessTokenRequest.getAccessToken());
            String firstName = obj.getString("given_name");
            String lastName = obj.getString("given_name");
            String userId =  UUID.randomUUID().toString();
            String email = obj.getString("email");
            UserAccount account=createOrUpdateUser(new UserAccount(userId,  email,  firstName,  lastName));
            return saveSessionInfo(userId);
        } catch (Exception e) {
            throw new GoogleSecurityException(e.getMessage());
        }
    }

    public void saveFeedbackInfo(String token, FeedBackForm feedBackForm) {
        String sessionID = SessionUtils.getSessionIdFromToken(token);
        SessionInfo sessionInfo = Optional.of(sessionRepository.getSessionInfo(sessionID))
                .orElseThrow(() -> {
                    logger.info("Session not found in DB" + sessionID);
                    return new SessionNotFoundException("Session not found" + sessionID);
                });
        List<FeedBackForm> feedBackForms = sessionInfo.getFeedBackForms();
        if (feedBackForms == null) {
            feedBackForms = new ArrayList<>();
        }
        feedBackForms.add(feedBackForm);
        sessionInfo.setFeedBackForms(feedBackForms);
        try {
            logger.info("feedbackInfo saved in DB " + objectMapper.writeValueAsString(feedBackForms));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        sessionRepository.save(sessionInfo);
    }

    public List<FeedBackForm> getFeedBackForm(String token) {
        String sessionID = SessionUtils.getSessionIdFromToken(token);
        SessionInfo sessionInfo = sessionRepository.getSessionInfo(sessionID);
        return sessionInfo.getFeedBackForms();
    }


}





