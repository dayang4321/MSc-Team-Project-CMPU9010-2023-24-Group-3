package com.docparser.springboot.model;

public class VersionInfo {

    private String url;
    private String versionID;
    private String eTag;

    public VersionInfo(String url, String versionID, String eTag) {
        this.url = url;
        this.versionID = versionID;
        this.eTag = eTag;

    }

    public VersionInfo() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

}

