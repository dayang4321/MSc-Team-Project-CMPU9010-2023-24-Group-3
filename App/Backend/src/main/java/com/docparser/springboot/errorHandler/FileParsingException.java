package com.docparser.springboot.errorHandler;

public class FileParsingException extends RuntimeException{
    public FileParsingException(String errorMessage) {
        super(errorMessage);
    }
}

