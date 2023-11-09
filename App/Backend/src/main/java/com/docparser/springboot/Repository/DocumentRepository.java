package com.docparser.springboot.Repository;

import com.docparser.springboot.model.DocumentInfo;
import com.docparser.springboot.model.ParagraphStyleInfo;
import com.docparser.springboot.model.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;


@Repository
public class DocumentRepository {
    @Autowired
    private DynamoDbEnhancedClient dynamoDbenhancedClient;

    public static final TableSchema<ParagraphStyleInfo> TABLE_SCHEMA_PARAGRAPH_STYLES = TableSchema.builder(ParagraphStyleInfo.class)
            .newItemSupplier(ParagraphStyleInfo::new)
            .addAttribute(String.class, a -> a.name("fontStyle")
                    .getter(ParagraphStyleInfo::getFontStyle)
                    .setter(ParagraphStyleInfo::setFontStyle))
            .addAttribute(String.class, a -> a.name("fontSize")
                    .getter(ParagraphStyleInfo::getFontSize)
                    .setter(ParagraphStyleInfo::setFontSize))
            .addAttribute(String.class, a -> a.name("fontColor")
                    .getter(ParagraphStyleInfo::getFontColor)
                    .setter(ParagraphStyleInfo::setFontColor))
            .addAttribute(String.class, a -> a.name("paragraphAlignment")
                    .getter(ParagraphStyleInfo::getParagraphAlignment)
                    .setter(ParagraphStyleInfo::setParagraphAlignment))
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
                            EnhancedType.documentOf(ParagraphStyleInfo.class, TABLE_SCHEMA_PARAGRAPH_STYLES)), a -> a.name("paragraphInfo")
                            .getter(DocumentInfo::getParagraphInfo)
                            .setter(DocumentInfo::setParagraphInfo))
                    .addAttribute(EnhancedType.listOf(
                            EnhancedType.documentOf(VersionInfo.class, TABLE_SCHEMA_VERSIONS)), a -> a.name("documentVersions")
                            .getter(DocumentInfo::getDocumentVersions)
                            .setter(DocumentInfo::setDocumentVersions))
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
