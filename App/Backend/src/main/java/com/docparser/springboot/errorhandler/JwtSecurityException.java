package com.docparser.springboot.errorhandler;

public class JwtSecurityException extends RuntimeException {
    public JwtSecurityException(String errorMessage) {
        super(errorMessage);
    }
}
