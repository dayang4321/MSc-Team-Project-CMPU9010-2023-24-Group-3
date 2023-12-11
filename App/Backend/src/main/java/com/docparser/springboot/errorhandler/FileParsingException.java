package com.docparser.springboot.errorhandler;

public class FileParsingException extends RuntimeException {
    public FileParsingException(String errorMessage) {
        super(errorMessage);
    }
}
