package com.docparser.springboot.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@DynamoDbBean
public class DocumentInfo {

    private String documentID;

    private String documentKey;

    private List<ParagraphStyleInfo> paragraphInfo;

    private List<VersionInfo> documentVersions;
    @DynamoDbAttribute("documentID")
    @DynamoDbPartitionKey
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
    @DynamoDbAttribute("paragraphInfo")
    public List<ParagraphStyleInfo> getParagraphInfo() {
        return paragraphInfo;
    }

    public void setParagraphInfo(List<ParagraphStyleInfo> paragraphInfo) {
        this.paragraphInfo = paragraphInfo;
    }
    @DynamoDbAttribute("documentVersions")
    public List<VersionInfo> getDocumentVersions() {
        return documentVersions;
    }

    public void setDocumentVersions(List<VersionInfo> documentVersions) {
        this.documentVersions = documentVersions;
    }

    @DynamoDbAttribute("documentKey")
    public String getDocumentKey() {
        return documentKey;
    }
    public  void setDocumentKey(String documentKey) {
        this.documentKey = documentKey;
    }
}
