package com.docparser.springboot.service;

import com.docparser.springboot.Repository.SessionRepository;
import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.SessionInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {
    @Autowired
    SessionRepository sessionRepository;
    private static final String SECRET_KEY = "ana7263nsnakka838";
    public String generateToken(String ipAddress, String sessionID){
    Instant now = Instant.now();
    Instant expirationTime = now.plusSeconds(3600);
        return Jwts.builder().setId(sessionID)
                .setSubject(ipAddress)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

}
    public String getIpAddressFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }
    public String getSessionIdFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getId();
    }


    public String generateAndSaveUserInfo(String ipAddress){
        SessionInfo session= new SessionInfo();
       String sessionID= UUID.randomUUID().toString();
        String token = generateToken(ipAddress,sessionID);
        session.setCreatedDate(Instant.now());
        session.setIpAddress(ipAddress);
        session.setTokenID(token);
        session.setSessionID(sessionID);
        sessionRepository.save(session);
        return token;
    }

    public SessionInfo saveFeedbackInfo(String token, FeedBackForm feedBackForm){
        String ipAddress= getIpAddressFromToken(token);
        SessionInfo session= sessionRepository.getSessionInfo(ipAddress);
        List<FeedBackForm> feedBackFormList =session.getFeedBackForms();
        if(feedBackFormList==null){
            feedBackFormList= new ArrayList<>();
        }
        feedBackFormList.add(feedBackForm);
        session.setFeedBackForms(feedBackFormList);
        sessionRepository.save(session);
        SessionInfo sessionInfo = sessionRepository.getSessionInfo(ipAddress);
        return sessionInfo;
    }
}


