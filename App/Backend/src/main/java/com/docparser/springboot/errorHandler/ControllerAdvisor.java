package com.docparser.springboot.errorHandler;

import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @ExceptionHandler({IOException.class, NullPointerException.class, IllegalStateException.class,RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Exception occurred  : "+ex);
        ErrorResponse errorResponse = new ErrorResponse(500, ex.getMessage(), ex.getStackTrace().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ServletException.class,SessionNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(ServletException ex, WebRequest request) {
        logger.error("Exception occurred message : "+ex);
        ErrorResponse errorResponse = new ErrorResponse(403, ex.getMessage(),ex.getStackTrace().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ FileParsingException.class})
    public ResponseEntity<ErrorResponse> handleSpecificExceptions(Exception ex) {
        logger.error("Exception occurred message : "+ex);
        ErrorResponse errorResponse = new ErrorResponse(400, ex.getMessage(),ex.getStackTrace().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}


