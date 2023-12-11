package com.docparser.springboot.errorhandler;

import jakarta.servlet.ServletException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Arrays;

// This annotation indicates that this class provides centralized exception handling across all @RequestMapping methods.
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    // Logger for logging error messages
    Logger log = LoggerFactory.getLogger(ControllerAdvisor.class);
    private static final String EXCEPTION_MESSAGE = "Exception occurred message : ";
    private static final String ACCESS_CONTROL_HEADER = "Access-Control-Allow-Origin";

    /*
     * Handles general exceptions like IO, Null Pointer, Illegal State, and Runtime
     * exceptions
     */
    @ExceptionHandler({IOException.class, NullPointerException.class, IllegalStateException.class,
            RuntimeException.class, FileParsingException.class})
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        // Log the exception details.
        log.error(EXCEPTION_MESSAGE, ex);

        // Create an error response with status code 500 and exception details
        ErrorResponse errorResponse = new ErrorResponse(500, ex.getMessage(), Arrays.toString(ex.getStackTrace()));

        /*
         * Return the response entity with HTTP status 500 (Internal Server Error) and
         * CORS header
         */
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(ACCESS_CONTROL_HEADER, "*") // or specify a specific origin
                .body(errorResponse);
    }

    // Handles exceptions related to servlets and authentication
    @ExceptionHandler({ServletException.class, AuthenticationException.class, GoogleSecurityException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(ServletException ex) {
        // Log the exception details.
        log.error(EXCEPTION_MESSAGE, ex);

        // Create an error response with status code 403 and exception details
        ErrorResponse errorResponse = new ErrorResponse(403, ex.getMessage(), Arrays.toString(ex.getStackTrace()));

        // Return the response entity with HTTP status 403 (Forbidden) and CORS header
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .header(ACCESS_CONTROL_HEADER, "*") // or specify a specific origin
                .body(errorResponse);
    }

    /*
     * Handles exceptions related to session not found, typically token validation
     * issues
     */
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTokenValidationException(ServletException ex) {
        // Log the exception details
        log.error(EXCEPTION_MESSAGE, ex);

        // Create an error response with status code 401 and exception details
        ErrorResponse errorResponse = new ErrorResponse(401, ex.getMessage(), Arrays.toString(ex.getStackTrace()));

        /*
         * Return the response entity with HTTP status 401 (Unauthorized) and CORS
         * header
         */
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(ACCESS_CONTROL_HEADER, "*") // or specify a specific origin
                .body(errorResponse);
    }

    /*
     * Handles specific exceptions like document not existing, user not found, or
     * duplicate uploads
     */
    @ExceptionHandler({DocumentNotExist.class, UserNotFoundException.class, DuplicateUpload.class})
    public ResponseEntity<ErrorResponse> handleSpecificExceptions(Exception ex) {
        // Log the exception details
        log.error(EXCEPTION_MESSAGE, ex);

        // Create an error response with status code 400 and exception details
        ErrorResponse errorResponse = new ErrorResponse(400, ex.getMessage(), Arrays.toString(ex.getStackTrace()));

        /*
         * Return the response entity with HTTP status 400 (Bad Request) and CORS header
         */
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header(ACCESS_CONTROL_HEADER, "*") // or specify a specific origin
                .body(errorResponse);
    }

}
