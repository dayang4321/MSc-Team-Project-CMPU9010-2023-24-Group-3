package com.docparser.springboot.model;

public class S3StorageInfo {
    private String documentID;
    private String url;
    private String key;
    private String versionID;


    // Constructors, getters, and setters
    public S3StorageInfo(String documentID, String url, String key, String versionID){
        this.documentID = documentID;
        this.url = url;
        this.key=key;
        this.versionID= versionID;
    }

    public S3StorageInfo() {
    }

    // Getters and setters
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setKey(String key) { this.key = key; }

    public String getKey() { return key; }
    public void setVersionID(String versionID) { this.versionID = versionID; }

    public String getVersionID() { return versionID; }
}
