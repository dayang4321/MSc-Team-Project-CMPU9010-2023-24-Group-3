package com.docparser.springboot.errorHandler;

import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleException(IOException ex) {
        ErrorResponse errorResponse = new ErrorResponse(500, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServletException.class )
    public ResponseEntity<Object> handleAccessDeniedException(
            ServletException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(403, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NullPointerException.class )
    public ResponseEntity<Object> handleNullPointerException(
            NullPointerException ex) {
        ErrorResponse errorResponse = new ErrorResponse(500, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(IllegalStateException.class )
    public ResponseEntity<Object> handleIllegalStateException(
            IllegalStateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(500, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
