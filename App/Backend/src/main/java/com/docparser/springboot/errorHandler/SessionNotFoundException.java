package com.docparser.springboot.errorHandler;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
