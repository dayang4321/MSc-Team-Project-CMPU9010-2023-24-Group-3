package com.docparser.springboot.utils;

import com.docparser.springboot.errorhandler.JwtSecurityException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class SessionUtils {

    private SessionUtils() {
        // Private constructor to prevent instantiation
    }
    private static String secretKey;

    @Value("${jwt.secret}")
    public void setSecretKey(String secretKey) {
        SessionUtils.secretKey = secretKey;
    }

    public static String generateToken(String sessionID, Date issuedAt, Date expirationTime) {
        // Generates a JWT token with given session ID, issue time, and expiration time.
        return Jwts.builder()
                .setId(sessionID)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationTime)
                /*
                 * Signing the JWT with the secret key using HS512 algorithm.
                 */
                .signWith(SignatureAlgorithm.HS512, secretKey)
                // Building the JWT and converting it to a compact, URL-safe string.
                .compact();
    }

    public static boolean validateToken(String token) {
        // Validates the JWT token. Returns true if valid, otherwise throws an
        // exception.
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            // Parsing the token with the secret key to validate it.
            return true;
        } catch (SignatureException | IllegalArgumentException | MalformedJwtException | ExpiredJwtException
                | UnsupportedJwtException ex) {
            throw new JwtSecurityException(ex.getMessage());
            // Catching various JWT exceptions and rethrowing them as runtime exceptions.
        }
    }

    public static Date getTime() {
        // Returns the current date and time.
        return Date.from(Instant.now());
    }

    public static Date getExpirationTime() {
        // Returns the expiration date and time, set to 24 hours from the current time.
        return Date.from(Instant.now().plusSeconds(24 * 60 * 60));
    }

    public static String getSessionIdFromToken(String token) {
        // Extracts and returns the session ID from the JWT token.
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getId();
    }

}
