package com.docparser.springboot.service;

import com.docparser.springboot.Repository.SessionRepository;
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



    public TokenResponse saveSessionInfo(String id) {
        Date expirationTime = SessionUtils.getExpirationTime();
        Date issuedAt = SessionUtils.getTime();
        String token = SessionUtils.generateToken(id, issuedAt, expirationTime);
        sessionRepository.save(new SessionInfo(id, token, Instant.now()));
        logger.info("token generated  and saved in DB" + token);
        return new TokenResponse(token, Instant.now().toString());
    }

    public TokenResponse generateAndSaveSessionInfo() {
        String sessionID = UUID.randomUUID().toString();
        return saveSessionInfo(sessionID);
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





