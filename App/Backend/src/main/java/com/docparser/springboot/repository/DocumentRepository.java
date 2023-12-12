package com.docparser.springboot.repository;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.model.DocumentInfo;
import com.docparser.springboot.model.VersionInfo;
import com.docparser.springboot.utils.ParsingUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DocumentRepository {

    private final DynamoDbEnhancedClient dynamoDbenhancedClient;

    private final DynamoDbClient dynamoDbClient;

    // Logger to log information
    Logger logger = LoggerFactory.getLogger(DocumentRepository.class);

    // Define schema for DocumentConfig table
    public static final TableSchema<DocumentConfig> DOCUMENT_CONFIG_PARAMS = TableSchema
            .builder(DocumentConfig.class)
            .newItemSupplier(DocumentConfig::new)

            // Define the attributes of the DocumentConfig table
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
            .addAttribute(Boolean.class, a -> a.name("paragraphSplitting")
                    .getter(DocumentConfig::getParagraphSplitting)
                    .setter(DocumentConfig::setParagraphSplitting))
            .addAttribute(Boolean.class, a -> a.name("headerGeneration")
                    .getter(DocumentConfig::getHeaderGeneration)
                    .setter(DocumentConfig::setHeaderGeneration))
            .addAttribute(Boolean.class, a -> a.name("removeItalics")
                    .getter(DocumentConfig::getRemoveItalics)
                    .setter(DocumentConfig::setRemoveItalics))
            .addAttribute(Boolean.class, a -> a.name("borderGeneration")
                    .getter(DocumentConfig::getBorderGeneration)
                    .setter(DocumentConfig::setBorderGeneration))
            .addAttribute(Boolean.class, a -> a.name("syllableSplitting")
                    .getter(DocumentConfig::getSyllableSplitting)
                    .setter(DocumentConfig::setSyllableSplitting))
            .addAttribute(Boolean.class, a -> a.name("handlePunctuations")
                    .getter(DocumentConfig::getHandlePunctuations)
                    .setter(DocumentConfig::setHandlePunctuations))
            .build();

    // Define the schema for VersionInfo table
    public static final TableSchema<VersionInfo> TABLE_SCHEMA_VERSIONS = TableSchema.builder(VersionInfo.class)
            .newItemSupplier(VersionInfo::new)

            // Define the attributes of the VersionInfo table
            .addAttribute(String.class, a -> a.name("eTag")
                    .getter(VersionInfo::getETag)
                    .setter(VersionInfo::setETag))
            .addAttribute(String.class, a -> a.name("versionID")
                    .getter(VersionInfo::getVersionID)
                    .setter(VersionInfo::setVersionID))
            .addAttribute(Instant.class, a -> a.name("createdDate")
                    .getter(VersionInfo::getCreatedDate)
                    .setter(VersionInfo::setCreatedDate))
            .build();

    // Define the schema for DocumentInfo table
    public static final TableSchema<DocumentInfo> DOCUMENT_INFO_TABLE_SCHEMA = TableSchema
            .builder(DocumentInfo.class)
            .newItemSupplier(DocumentInfo::new)

            // Define the attributes of the DocumentInfo table
            .addAttribute(String.class, a -> a.name("documentID")
                    .getter(DocumentInfo::getDocumentID)
                    .setter(DocumentInfo::setDocumentID)
                    .addTag(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("documentKey")
                    .getter(DocumentInfo::getDocumentKey)
                    .setter(DocumentInfo::setDocumentKey))
            .addAttribute(Instant.class, a -> a.name("createdDate")
                    .getter(DocumentInfo::getCreatedDate)
                    .setter(DocumentInfo::setCreatedDate))
            .addAttribute(Instant.class, a -> a.name("expirationTime")
                    .getter(DocumentInfo::getExpirationTime)
                    .setter(DocumentInfo::setExpirationTime))
            .addAttribute(EnhancedType.listOf(
                    EnhancedType.documentOf(VersionInfo.class, TABLE_SCHEMA_VERSIONS)),
                    a -> a.name("documentVersions")
                            .getter(DocumentInfo::getDocumentVersions)
                            .setter(DocumentInfo::setDocumentVersions))
            .addAttribute(EnhancedType.documentOf(DocumentConfig.class, DOCUMENT_CONFIG_PARAMS),
                    a -> a.name("documentConfig") // DocumentConfig.class
                            .getter(DocumentInfo::getDocumentConfig)
                            .setter(DocumentInfo::setDocumentConfig))
            .build();

    // Method to get the DynamoDbTable instance for DocumentInfo
    private DynamoDbTable<DocumentInfo> getTable() {
        // Create a tablescheme to scan our bean class order
        return dynamoDbenhancedClient.table("DocumentInfo", DOCUMENT_INFO_TABLE_SCHEMA);
    }

    // Save a DocumentInfo record to the database
    public void save(DocumentInfo documentInfo) {
        DynamoDbTable<DocumentInfo> documentInfoTable = getTable();

        documentInfoTable.putItem(documentInfo);
    }

    // Retrieve a DocumentInfo record by its ID
    public Optional<DocumentInfo> getDocumentInfo(String documentID) {
        DynamoDbTable<DocumentInfo> documentInfoTable = getTable();
        // Construct the key with partition and sort key
        Key key = Key.builder().partitionValue(documentID).build();
        return Optional.of(documentInfoTable.getItem(key));
    }

    public Map<String, Set<String>> getDocumentsExpired() {
        String expirationTime = Instant.now().toString();
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

        expressionAttributeValues.put(":val", AttributeValue.builder().s(expirationTime).build());

        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("DocumentInfo")
                .filterExpression("expirationTime <= :val")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
        ScanResponse response = dynamoDbClient.scan(scanRequest);
        if (response.items().isEmpty()) {
            return new HashMap<>();
        }
        if(response.items().size() == 1 && response.items().get(0).get("documentID")==null) {
            return new HashMap<>();
        }
        Set<String> documentIDs = response.items().stream().map(item -> item.get("documentID").s()).collect(Collectors.toSet());
        Set<String> documentkeys = response.items().stream().map(item -> item.get("documentKey").s()).collect(Collectors.toSet());
        HashMap<String, Set<String>> documentMap = new HashMap<>();
        documentMap.put("documentIDs", documentIDs);
        documentMap.put("documentKeys", documentkeys);

        return documentMap;
    }

    public void deleteSingleDocument(String documentID) {
        DynamoDbTable<DocumentInfo> documentInfoTable = getTable();
        // Construct the key with partition and sort key
        Key key = Key.builder().partitionValue(documentID).build();
        documentInfoTable.deleteItem(key);
    }

    public Set<String> getDocumentKeys(Set<String> documentIDs) {
        Set<String> documentKeys = new HashSet<>();
        if (documentIDs.isEmpty()) {
            return documentKeys;
        }
        BatchGetItemRequest batchGetItemRequest = BatchGetItemRequest.builder()
                .requestItems(Collections.singletonMap("DocumentInfo", KeysAndAttributes.builder()
                        .keys(documentIDs.stream().map(id -> Collections.singletonMap("documentID", AttributeValue.builder().s(id).build())).toList())
                        .build()))
                .build();
        BatchGetItemResponse batchGetItemResponse = dynamoDbClient.batchGetItem(batchGetItemRequest);
        List<Map<String, AttributeValue>> items = batchGetItemResponse.responses().get("DocumentInfo");
        for (Map<String, AttributeValue> item : items) {
            documentKeys.add(item.get("documentKey").s());
        }

        return documentKeys;
    }

    public void deleteDocument(Set<String> documentIDs) {
        logger.info("deleting documents ID's from db");
        if (!documentIDs.isEmpty()) {
            List<WriteRequest> requests = new ArrayList<>();

            List<List<String>> documentIdBatchList = ParsingUtils.partitionList(documentIDs.stream().toList());
            for (List<String> dList : documentIdBatchList) {
                for (String id : dList) {
                    AttributeValue value = AttributeValue.builder().s(id).build();
                    DeleteRequest request = DeleteRequest.builder()
                            .key(Collections.singletonMap("documentID", value)).build();
                    requests.add(WriteRequest.builder().deleteRequest(request).build());
                }

                dynamoDbClient.batchWriteItem(BatchWriteItemRequest.builder()
                        .requestItems(Collections.singletonMap("DocumentInfo", requests))
                        .build());
            }
        }
    }
}
