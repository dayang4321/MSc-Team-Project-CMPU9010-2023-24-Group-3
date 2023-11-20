package com.docparser.springboot.service;

import com.docparser.springboot.Repository.SessionRepository;
import com.docparser.springboot.errorHandler.SessionNotFoundException;
import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.SessionInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class SessionService {
    Logger logger = LoggerFactory.getLogger(SessionService.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    SessionRepository sessionRepository;
    private static final String SECRET_KEY = "ana7263nsnakka838";

    public String generateToken(String sessionID,Date issuedAt,Date expirationTime) {
        return Jwts.builder().setId(sessionID)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();


    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (SignatureException | IllegalArgumentException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public String getSessionIdFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getId();
    }


    public HashMap<String, Object> generateAndSaveUserInfo() {
        String sessionID = UUID.randomUUID().toString();
        Instant expirationTime = Instant.now().plusSeconds(24 * 60 * 60);
        Instant issuedAt = Instant.now();
        String token = generateToken(sessionID, Date.from(issuedAt),Date.from(expirationTime));
        sessionRepository.save(new SessionInfo(sessionID, "", token, Instant.now()));
        logger.info("token generated  and saved in DB" + token);
        HashMap<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("expiry", expirationTime.toString());
        return response;
    }

    public void saveFeedbackInfo(String token, FeedBackForm feedBackForm) {
        String sessionID = getSessionIdFromToken(token);
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
        String sessionID = getSessionIdFromToken(token);
        SessionInfo sessionInfo = sessionRepository.getSessionInfo(sessionID);
        return sessionInfo.getFeedBackForms();
    }
}





