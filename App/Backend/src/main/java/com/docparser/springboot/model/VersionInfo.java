package com.docparser.springboot.model;

import java.time.Instant;

public class VersionInfo {

    private String versionID;
    private String eTag;

    private Instant createdDate;

    public VersionInfo( String versionID, String eTag, Instant createdDate) {
        this.versionID = versionID;
        this.eTag = eTag;
        this.createdDate = createdDate;

    }

    public VersionInfo() {

    }


    public String getVersionID() {
        return versionID;
    }

    public void setVersionID(String versionID) {
        this.versionID = versionID;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}

