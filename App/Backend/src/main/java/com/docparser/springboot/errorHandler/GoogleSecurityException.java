package com.docparser.springboot.errorHandler;

public class GoogleSecurityException  extends RuntimeException{
    public GoogleSecurityException(String errorMessage) {
        super(errorMessage);
    }
}
