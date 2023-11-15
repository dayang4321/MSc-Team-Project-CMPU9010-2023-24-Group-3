package com.docparser.springboot.Repository;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.model.DocumentInfo;
import com.docparser.springboot.model.ParagraphStyleInfo;
import com.docparser.springboot.model.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;

import java.time.Instant;


@Repository
public class DocumentRepository {
    @Autowired
    private DynamoDbEnhancedClient dynamoDbenhancedClient;
    private  String fontType;
    private  String fontSize;
    private  String fontColor;
    private  String backgroundColor;
    private  String lineSpacing;
    private  String characterSpacing;
    private  String alignment;
    private  Boolean generateTOC;
    private  Boolean removeItalics;

    public static final TableSchema<DocumentConfig> DOCUMENT_CONFIG_PARAMS = TableSchema.builder(DocumentConfig.class)
            .newItemSupplier(DocumentConfig::new)
            .addAttribute(String.class, a -> a.name("fontType")
                    .getter(DocumentConfig::getFontType)
                    .setter(DocumentConfig::setFontType))
            .addAttribute(String.class, a -> a.name("fontSize")
                    .getter(DocumentConfig::getFontSize)
                    .setter(DocumentConfig::setFontSize))
            .addAttribute(String.class, a -> a.name("fontColor")
                    .getter(DocumentConfig::getFontColor)
                    .setter(DocumentConfig::setFontColor))
            .addAttribute(String.class, a -> a.name("backgroundColor")
                    .getter(DocumentConfig::getBackgroundColor)
                    .setter(DocumentConfig::setBackgroundColor))
            .addAttribute(String.class, a -> a.name("lineSpacing")
                    .getter(DocumentConfig::getLineSpacing)
                    .setter(DocumentConfig::setLineSpacing))
            .addAttribute(String.class, a -> a.name("characterSpacing")
                    .getter(DocumentConfig::getCharacterSpacing)
                    .setter(DocumentConfig::setCharacterSpacing))
            .addAttribute(String.class, a -> a.name("alignment")
                    .getter(DocumentConfig::getAlignment)
                    .setter(DocumentConfig::setAlignment))
            .addAttribute(Boolean.class, a -> a.name("generateTOC")
                    .getter(DocumentConfig::getGenerateTOC)
                    .setter(DocumentConfig::setGenerateTOC))
            .addAttribute(Boolean.class, a -> a.name("removeItalics")
                    .getter(DocumentConfig::getRemoveItalics)
                    .setter(DocumentConfig::setRemoveItalics))
            .build();
    public static final TableSchema<VersionInfo> TABLE_SCHEMA_VERSIONS = TableSchema.builder(VersionInfo.class)
            .newItemSupplier(VersionInfo::new)
            .addAttribute(String.class, a -> a.name("eTag")
                    .getter(VersionInfo::geteTag)
                    .setter(VersionInfo::seteTag))
            .addAttribute(String.class, a -> a.name("versionID")
                    .getter(VersionInfo::getVersionID)
                    .setter(VersionInfo::setVersionID))
            .addAttribute(String.class, a -> a.name("url")
                    .getter(VersionInfo::getUrl)
                    .setter(VersionInfo::setUrl))
            .addAttribute(Instant.class, a -> a.name("createdDate")
                    .getter(VersionInfo::getCreatedDate)
                    .setter(VersionInfo::setCreatedDate))
            .build();

    public static final TableSchema<DocumentInfo> DOCUMENT_INFO_TABLE_SCHEMA =
            TableSchema.builder(DocumentInfo.class)
                    .newItemSupplier(DocumentInfo::new)
                    .addAttribute(String.class, a -> a.name("documentID")
                            .getter(DocumentInfo::getDocumentID)
                            .setter(DocumentInfo::setDocumentID)
                            .addTag(StaticAttributeTags.primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name("documentKey")
                            .getter(DocumentInfo::getDocumentKey)
                            .setter(DocumentInfo::setDocumentKey))
                    .addAttribute(EnhancedType.listOf(
                            EnhancedType.documentOf(VersionInfo.class, TABLE_SCHEMA_VERSIONS)), a -> a.name("documentVersions")
                            .getter(DocumentInfo::getDocumentVersions)
                            .setter(DocumentInfo::setDocumentVersions))
                    .addAttribute(EnhancedType.documentOf(DocumentConfig.class, DOCUMENT_CONFIG_PARAMS), a -> a.name("documentConfig")  // DocumentConfig.class
                            .getter(DocumentInfo::getDocumentConfig)
                            .setter(DocumentInfo::setDocumentConfig))
                    .build();
    private DynamoDbTable<DocumentInfo> getTable() {
        // Create a tablescheme to scan our bean class order
        return dynamoDbenhancedClient.table("DocumentInfo", DOCUMENT_INFO_TABLE_SCHEMA);
    }

    public void save(DocumentInfo documentInfo) {
        DynamoDbTable<DocumentInfo> documentInfoTable = getTable();
        documentInfoTable.putItem(documentInfo);

    }

    public DocumentInfo getDocumentInfo(String documentID) {
        DynamoDbTable<DocumentInfo> documentInfoTable = getTable();
        // Construct the key with partition and sort key
        Key key = Key.builder().partitionValue(documentID).build();
        return documentInfoTable.getItem(key);
    }
}
