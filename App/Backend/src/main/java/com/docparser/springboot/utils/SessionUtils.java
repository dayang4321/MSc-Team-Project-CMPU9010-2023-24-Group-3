package com.docparser.springboot.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class SessionUtils {

    private static final String SECRET_KEY = "ana7263nsnakka838";
    // SECRET_KEY: A private constant used for signing and verifying JWT tokens.

    public static String generateToken(String sessionID, Date issuedAt, Date expirationTime) {
        // Generates a JWT token with given session ID, issue time, and expiration time.
        return Jwts.builder()
                .setId(sessionID)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationTime)
                /*
                 * Signing the JWT with the secret key using HS512 algorithm.
                 */
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                // Building the JWT and converting it to a compact, URL-safe string.
                .compact();
    }

    public static boolean validateToken(String token) {
        // Validates the JWT token. Returns true if valid, otherwise throws an
        // exception.
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            // Parsing the token with the secret key to validate it.
            return true;
        } catch (SignatureException | IllegalArgumentException | MalformedJwtException | ExpiredJwtException
                | UnsupportedJwtException ex) {
            throw new RuntimeException(ex.getMessage());
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
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getId();
    }

}
