package com.docparser.springboot.model;



import java.util.List;


public class DocumentResponse {

    private String documentID;

    private String documentKey;

    private DocumentConfig documentConfig;

    private  String documentOriginalVersion;


    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public DocumentConfig getDocumentConfig() {
        return documentConfig;
    }

    public void setDocumentConfig(DocumentConfig documentConfig) {
        this.documentConfig = documentConfig;
    }


    public String getDocumentOriginalVersion() {
        return documentOriginalVersion;
    }

    public void setDocumentOriginalVersion(String documentOriginalVersion) {
        this.documentOriginalVersion = documentOriginalVersion;
    }

    public String getDocumentKey() {
        return documentKey;
    }
    public  void setDocumentKey(String documentKey) {
        this.documentKey = documentKey;
    }
}
