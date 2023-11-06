package com.docparser.springboot.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@DynamoDbBean
public class DocumentInfo {

    private String documentID;

    private List<ParagraphStyleInfo> paragraphInfo;

    private List<String> documentVersions;
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
    public List<String> getDocumentVersions() {
        return documentVersions;
    }

    public void setDocumentVersions(List<String> documentVersions) {
        this.documentVersions = documentVersions;
    }
}
