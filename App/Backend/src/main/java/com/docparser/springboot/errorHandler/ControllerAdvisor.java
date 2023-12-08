package com.docparser.springboot.errorHandler;

import jakarta.servlet.ServletException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

// This annotation indicates that this class provides centralized exception handling across all @RequestMapping methods.
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    // Logger for logging error messages
    Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    /*
     * Handles general exceptions like IO, Null Pointer, Illegal State, and Runtime
     * exceptions
     */
    @ExceptionHandler({ IOException.class, NullPointerException.class, IllegalStateException.class,
            RuntimeException.class, FileParsingException.class })
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        // Log the exception details.
        logger.error("Exception occurred  : " + ex);

        // Create an error response with status code 500 and exception details
        ErrorResponse errorResponse = new ErrorResponse(500, ex.getMessage(), ex.getStackTrace().toString());

        /*
         * Return the response entity with HTTP status 500 (Internal Server Error) and
         * CORS header
         */
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Access-Control-Allow-Origin", "*") // or specify a specific origin
                .body(errorResponse);
    }

    // Handles exceptions related to servlets and authentication
    @ExceptionHandler({ ServletException.class, AuthenticationException.class, GoogleSecurityException.class })
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(ServletException ex, WebRequest request) {
        // Log the exception details.
        logger.error("Exception occurred message : " + ex);

        // Create an error response with status code 403 and exception details
        ErrorResponse errorResponse = new ErrorResponse(403, ex.getMessage(), ex.getStackTrace().toString());

        // Return the response entity with HTTP status 403 (Forbidden) and CORS header
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .header("Access-Control-Allow-Origin", "*") // or specify a specific origin
                .body(errorResponse);
    }

    /*
     * Handles exceptions related to session not found, typically token validation
     * issues
     */
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTokenValidationException(ServletException ex, WebRequest request) {
        // Log the exception details
        logger.error("Exception occurred message : " + ex);

        // Create an error response with status code 401 and exception details
        ErrorResponse errorResponse = new ErrorResponse(401, ex.getMessage(), ex.getStackTrace().toString());

        /*
         * Return the response entity with HTTP status 401 (Unauthorized) and CORS
         * header
         */
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("Access-Control-Allow-Origin", "*") // or specify a specific origin
                .body(errorResponse);
    }

    /*
     * Handles specific exceptions like document not existing, user not found, or
     * duplicate uploads
     */
    @ExceptionHandler({ DocumentNotExist.class, UserNotFoundException.class, DuplicateUpload.class })
    public ResponseEntity<ErrorResponse> handleSpecificExceptions(Exception ex) {
        // Log the exception details
        logger.error("Exception occurred message : " + ex);

        // Create an error response with status code 400 and exception details
        ErrorResponse errorResponse = new ErrorResponse(400, ex.getMessage(), ex.getStackTrace().toString());

        /*
         * Return the response entity with HTTP status 400 (Bad Request) and CORS header
         */
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Access-Control-Allow-Origin", "*") // or specify a specific origin
                .body(errorResponse);
    }

}
