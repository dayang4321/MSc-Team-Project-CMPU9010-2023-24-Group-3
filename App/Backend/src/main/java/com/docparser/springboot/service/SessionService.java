package com.docparser.springboot.service;

import com.docparser.springboot.Repository.SessionRepository;
import com.docparser.springboot.Repository.UserRepository;
import com.docparser.springboot.errorHandler.SessionNotFoundException;
import com.docparser.springboot.model.*;
import com.docparser.springboot.utils.SessionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SessionService {
    Logger logger = LoggerFactory.getLogger(SessionService.class);
    private final ObjectMapper objectMapper;
    private final SessionRepository sessionRepository;

    // Saves session information to the database
    public void saveSessionInfo(SessionInfo sessionInfo) {
        sessionRepository.save(sessionInfo);
    }

    // Generates and saves a new session with a unique token
    public TokenResponse generateAndSaveSessionInfo() {
        String sessionID = UUID.randomUUID().toString();
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setSessionID(sessionID);
        sessionInfo.setCreatedDate(Instant.now());

        // Generate a token for the session
        String token = SessionUtils.generateToken(sessionID, SessionUtils.getTime(), SessionUtils.getExpirationTime());
        sessionInfo.setTokenID(token);
        saveSessionInfo(sessionInfo);

        // Return the generated token and creation time
        return new TokenResponse(token, SessionUtils.getTime().toInstant().toString());
    }

    // Saves feedback information associated with a specific session
    public void saveFeedbackInfo(String token, FeedBackForm feedBackForm) {
        String sessionID = SessionUtils.getSessionIdFromToken(token);

        // Retrieve session information from the database, throw exception if not found
        SessionInfo sessionInfo = Optional.of(sessionRepository.getSessionInfo(sessionID))
                .orElseThrow(() -> {
                    logger.info("Session not found in DB" + sessionID);
                    return new SessionNotFoundException("Session not found" + sessionID);
                });

        // Retrieve or initialize the feedback forms list
        List<FeedBackForm> feedBackForms = sessionInfo.getFeedBackForms();
        if (feedBackForms == null) {
            feedBackForms = new ArrayList<>();
        }
        feedBackForms.add(feedBackForm);
        sessionInfo.setFeedBackForms(feedBackForms);

        // Log the saved feedback information
        try {
            logger.info("feedbackInfo saved in DB " + objectMapper.writeValueAsString(feedBackForms));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        sessionRepository.save(sessionInfo);
    }

    // Retrieves feedback forms associated with a specific token
    public List<FeedBackForm> getFeedBackForm(String token) {
        String sessionID = SessionUtils.getSessionIdFromToken(token);
        SessionInfo sessionInfo = sessionRepository.getSessionInfo(sessionID);
        return sessionInfo.getFeedBackForms();
    }

    // Retrieves session information for a given session ID
    public Optional<SessionInfo> getSessionInfo(String sessionID) {
        return Optional.ofNullable(sessionRepository.getSessionInfo(sessionID));
    }

    // Deletes a session based on session ID
    public void deleteSession(String sessionID) {
        sessionRepository.deleteUserSession(sessionID);
    }

}
