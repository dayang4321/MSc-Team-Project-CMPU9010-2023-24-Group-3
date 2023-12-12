package com.docparser.springboot.repository;

import com.docparser.springboot.errorhandler.UserNotFoundException;
import com.docparser.springboot.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Static imports for shared constants
import static com.docparser.springboot.repository.DocumentRepository.DOCUMENT_CONFIG_PARAMS;
import static com.docparser.springboot.repository.SessionRepository.TABLE_SCHEMA_FEEDBACKFORM;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final DynamoDbEnhancedClient dynamoDbenhancedClient;

    private final DynamoDbClient dynamoDbClient;

    // Define the TableSchema for UserDocument
    public static final TableSchema<UserDocument> TABLE_SCHEMA_DOCUMENTS = TableSchema.builder(UserDocument.class)
            // Supplier for new instances of UserDocument
            .newItemSupplier(UserDocument::new)
            .addAttribute(String.class, a -> a.name("documentID")
                    .getter(UserDocument::getDocumentID)
                    .setter(UserDocument::setDocumentID))
            .addAttribute(String.class, a -> a.name("documentKey")
                    .getter(UserDocument::getDocumentKey)
                    .setter(UserDocument::setDocumentKey))
            .addAttribute(Instant.class, a -> a.name("createdDate")
                    .getter(UserDocument::getCreatedDate)
                    .setter(UserDocument::setCreatedDate))
            .addAttribute(Instant.class, a -> a.name("expirationTime")
                    .getter(UserDocument::getExpirationTime)
                    .setter(UserDocument::setExpirationTime))
            .build();

    // Define the TableSchema for UserAccount
    public static final TableSchema<UserAccount> TABLE_SCHEMA_USER = TableSchema.builder(UserAccount.class)
            // Supplier for new instances of UserAccount
            .newItemSupplier(UserAccount::new)
            // Map 'userId' attribute as primary partition key
            .addAttribute(String.class, a -> a.name("userId")
                    .getter(UserAccount::getUserId)
                    .setter(UserAccount::setUserId)
                    .addTag(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("email")
                    .getter(UserAccount::getEmail)
                    .setter(UserAccount::setEmail))
            .addAttribute(String.class, a -> a.name("username")
                    .getter(UserAccount::getUsername)
                    .setter(UserAccount::setUsername))
            .addAttribute(String.class, a -> a.name("provider")
                    .getter(UserAccount::getProvider)
                    .setter(UserAccount::setProvider))
            // Map 'userDocuments' attribute as a list of UserDocuments
            .addAttribute(EnhancedType
                            .listOf(EnhancedType.documentOf(UserDocument.class, TABLE_SCHEMA_DOCUMENTS)),
                    a -> a.name("userDocuments")
                            .getter(UserAccount::getUserDocuments)
                            .setter(UserAccount::setUserDocuments))
            // Map 'userPresets' attribute as a DocumentConfig
            .addAttribute(EnhancedType.documentOf(DocumentConfig.class, DOCUMENT_CONFIG_PARAMS),
                    a -> a.name("userPresets")
                            .getter(UserAccount::getUserPresets)
                            .setter(UserAccount::setUserPresets))
            // Map 'feedBackForms' attribute as a list of FeedBackForms
            .addAttribute(EnhancedType
                            .listOf(EnhancedType.documentOf(FeedBackForm.class, TABLE_SCHEMA_FEEDBACKFORM)),
                    a -> a.name("feedBackForms")
                            .getter(UserAccount::getFeedBackForms)
                            .setter(UserAccount::setFeedBackForms))
            .build();

    private DynamoDbTable<UserAccount> getTable() {
        // Create a tablescheme to scan our bean class order
        // Get the DynamoDB table instance for 'Users' with the defined schema
        return dynamoDbenhancedClient.table("Users", TABLE_SCHEMA_USER);
    }

    // Save a UserAccount instance to the DynamoDB table
    public void saveUser(UserAccount userAccount) {
        DynamoDbTable<UserAccount> sessionTable = getTable();
        sessionTable.putItem(userAccount);
    }

    public Optional<UserAccount> getUserInfo(String id) {
        try {
            // Retrieve user information based on the provided ID
            DynamoDbTable<UserAccount> userAccountDynamoDbTable = getTable();
            // Construct the key with partition and sort key
            Key key = Key.builder().partitionValue(id).build();

            return Optional.ofNullable(userAccountDynamoDbTable.getItem(key));
        } catch (Exception e) {
            // Handle exceptions by throwing a custom UserNotFoundException
            throw new UserNotFoundException("User not found" + e.getMessage());
        }
    }

    // Retrieve user information based on email
    public Optional<UserAccount> getUserInfobyEmail(String email) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        DynamoDbTable<UserAccount> userAccountDynamoDbTable = getTable();
        expressionAttributeValues.put(":val", AttributeValue.builder().s(email).build());
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Users")
                .filterExpression("email = :val")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
        ScanResponse response = dynamoDbClient.scan(scanRequest);
        Map<String, AttributeValue> item = response.items().stream().findFirst().orElse(null);

        if (item == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(userAccountDynamoDbTable
                .getItem(Key.builder().partitionValue(item.get("userId").s()).build()));

    }
    public void deleteUser(String userId) {
        AttributeValue value = AttributeValue.builder().s(userId).build();
        DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName("Users")
                .key(Collections.singletonMap("userId", value))
                .build());
        logger.info("user deleted:{}" ,deleteItemResponse);
    }

}
