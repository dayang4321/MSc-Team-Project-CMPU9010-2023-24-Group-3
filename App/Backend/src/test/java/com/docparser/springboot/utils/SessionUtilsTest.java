package com.docparser.springboot.utils;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class SessionUtilsTest {

    private static final String SESSION_ID = "sampleSessionId";

    @Test
    void testGenerateAndValidateToken() {
        Date issuedAt = SessionUtils.getTime();
        Date expirationTime = SessionUtils.getExpirationTime();
        ReflectionTestUtils.setField(SessionUtils.class, "secretKey", "yourMockedSecretKey");


        // Generate a token
        String token = SessionUtils.generateToken(SESSION_ID, issuedAt, expirationTime);

        // Validate the generated token
        assertTrue(SessionUtils.validateToken(token));
    }

    @Test
    void testValidateInvalidToken() {
        // Attempt to validate an invalid token
        ReflectionTestUtils.setField(SessionUtils.class, "secretKey", "yourMockedSecretKey");

        assertThrows(RuntimeException.class, () -> SessionUtils.validateToken("invalidToken"));
    }

    @Test
    void testGetSessionIdFromToken() {
        Date issuedAt = SessionUtils.getTime();
        Date expirationTime = SessionUtils.getExpirationTime();

        // Generate a token
        ReflectionTestUtils.setField(SessionUtils.class, "secretKey", "yourMockedSecretKey");

        String token = SessionUtils.generateToken(SESSION_ID, issuedAt, expirationTime);

        // Extract session ID from the generated token
        String extractedSessionId = SessionUtils.getSessionIdFromToken(token);

        // Check if the extracted session ID matches the original session ID
        assertEquals(SESSION_ID, extractedSessionId);
    }

    @Test
    void testGetTime() {
        // Get the current time
        Date currentTime = SessionUtils.getTime();

        // Ensure that the current time is not null
        assertNotNull(currentTime);
    }

    @Test
    void testGetExpirationTime() {
        // Get the expiration time
        Date expirationTime = SessionUtils.getExpirationTime();

        // Ensure that the expiration time is not null
        assertNotNull(expirationTime);
    }
}
