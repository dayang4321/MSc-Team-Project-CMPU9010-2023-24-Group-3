package com.docparser.springboot.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.docparser.springboot.Repository.SessionRepository;
import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.SessionInfo;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class SessionService {
    @Autowired
    SessionRepository sessionRepository;
    private static final String SECRET_KEY = "ana7263nsnakka838";

    public String generateToken(String sessionID) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(3600);
        return Jwts.builder().setId(sessionID)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
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


    public String generateAndSaveUserInfo(String ipAddress) {
        SessionInfo session = sessionRepository.getSessionInfo(ipAddress);
        if (session != null) {
            String savedToken = session.getTokenID();
            DecodedJWT decodedJWT = JWT.decode(savedToken);
            Date expiresAt = decodedJWT.getExpiresAt();
            if (expiresAt.before(new Date())) {
                String newtoken = generateToken(session.getSessionID());
                session.setTokenID(newtoken);
                sessionRepository.save(session);
                return newtoken;
            }
            return savedToken;
        }
        session = new SessionInfo();
        String sessionID = UUID.randomUUID().toString();
        String token = generateToken(sessionID);
        session.setCreatedDate(Instant.now());
        session.setIpAddress(ipAddress);
        session.setTokenID(token);
        session.setSessionID(sessionID);
        sessionRepository.save(session);
        return token;
    }

    public void saveFeedbackInfo(String token, FeedBackForm feedBackForm) {
        String sessionID = getSessionIdFromToken(token);

        Optional.ofNullable(sessionRepository.getSessionInfo(sessionID))
                .ifPresent(session -> {
                    List<FeedBackForm> feedBackFormList = Optional.ofNullable(session.getFeedBackForms())
                            .orElseGet(ArrayList::new);
                    feedBackFormList.add(feedBackForm);
                    session.setFeedBackForms(feedBackFormList);
                    sessionRepository.save(session);
                });
    }
    public List<FeedBackForm> getFeedBackForm(String token) {
        String sessionID = getSessionIdFromToken(token);
        SessionInfo sessionInfo = sessionRepository.getSessionInfo(sessionID);
        return sessionInfo.getFeedBackForms();
    }
}





