package com.docparser.springboot.errorHandler;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

