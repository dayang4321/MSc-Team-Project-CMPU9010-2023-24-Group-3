package com.docparser.springboot.errorhandler;

public class GoogleSecurityException extends RuntimeException {
    public GoogleSecurityException(String errorMessage) {
        super(errorMessage);
    }
}
