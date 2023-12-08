package com.docparser.springboot.model;

/**
 * Class representing the storage information of a document in AWS S3.
 */
public class S3StorageInfo {
    // Variables to store various S3 document attributes
    private String documentID;
    private String url;
    private String key;
    private String versionID;

    /**
     * Constructor to create a new S3StorageInfo instance with specified details.
     *
     * @param documentID The unique identifier of the document.
     * @param url        The URL of the stored document.
     * @param key        The key associated with the document in S3.
     * @param versionID  The version ID of the document in S3.
     */
    public S3StorageInfo(String documentID, String url, String key, String versionID) {
        this.documentID = documentID;
        this.url = url;
        this.key = key;
        this.versionID = versionID;
    }

    public S3StorageInfo() {
    }

    // Getters and setters for accessing and modifying the properties
    /**
     * Gets the document ID.
     *
     * @return The document ID.
     */
    public String getDocumentID() {
        return documentID;
    }

    /**
     * Sets the document ID.
     *
     * @param documentID The document ID to set.
     */
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    /**
     * Gets the URL of the document.
     *
     * @return The URL of the document.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the document.
     *
     * @param url The URL to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the key of the document.
     *
     * @param key The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the key of the document.
     *
     * @return The key of the document.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the version ID of the document.
     *
     * @param versionID The version ID to set.
     */
    public void setVersionID(String versionID) {
        this.versionID = versionID;
    }

    /**
     * Gets the version ID of the document.
     *
     * @return The version ID of the document.
     */
    public String getVersionID() {
        return versionID;
    }
}
