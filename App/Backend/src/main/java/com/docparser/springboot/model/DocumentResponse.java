package com.docparser.springboot.model;


import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.HashMap;
import java.util.List;


public class DocumentResponse {

    private String documentID;

    private String documentKey;

    private DocumentConfig documentConfig;

    private HashMap<String, String> versions;


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


    public HashMap<String, String> getVersions() {
        return versions;
    }

    public void setVersions(HashMap<String, String> versions) {
        this.versions = versions;
    }

    public String getDocumentKey() {
        return documentKey;
    }

    public void setDocumentKey(String documentKey) {
        this.documentKey = documentKey;
    }
}
