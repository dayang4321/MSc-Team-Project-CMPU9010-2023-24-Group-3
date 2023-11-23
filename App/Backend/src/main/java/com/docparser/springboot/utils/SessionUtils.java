package com.docparser.springboot.utils;

import io.jsonwebtoken.*;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class SessionUtils {
    private static final String SECRET_KEY = "ana7263nsnakka838";

    public static String generateToken(String sessionID, Date issuedAt, Date expirationTime) {
        return Jwts.builder().setId(sessionID)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (SignatureException | IllegalArgumentException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static String getSessionIdFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getId();
    }
    public static JSONObject getUserInfoFromGoogleOauthApi(String token) {
        HttpHeaders headers = new HttpHeaders();
        String url="https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<Object> entity=new HttpEntity<Object>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return new JSONObject(response.getBody());
    }

}