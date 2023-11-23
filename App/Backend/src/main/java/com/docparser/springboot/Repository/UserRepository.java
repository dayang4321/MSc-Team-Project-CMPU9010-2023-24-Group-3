package com.docparser.springboot.Repository;

import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.SessionInfo;
import com.docparser.springboot.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDbEnhancedClient dynamoDbenhancedClient;
    @Autowired
    private DynamoDbClient dynamoDbClient;

    public static final TableSchema<UserAccount> TABLE_SCHEMA_USER = TableSchema.builder(UserAccount.class)
            .newItemSupplier(UserAccount::new)
            .addAttribute(String.class, a -> a.name("userId")
                    .getter(UserAccount::getUserId)
                    .setter(UserAccount::setUserId)
                    .addTag(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("email")
                    .getter(UserAccount::getEmail)
                    .setter(UserAccount::setEmail))
            .addAttribute(String.class, a -> a.name("firstName")
                    .getter(UserAccount::getFirstName)
                    .setter(UserAccount::setFirstName))
            .addAttribute(String.class, a -> a.name("lastName")
                    .getter(UserAccount::getLastName)
                    .setter(UserAccount::setLastName))
            .build();


    private DynamoDbTable<UserAccount> getTable() {
        // Create a tablescheme to scan our bean class order
        return dynamoDbenhancedClient.table("Users", TABLE_SCHEMA_USER);
    }

    public void saveUser(UserAccount userAccount) {
        DynamoDbTable<UserAccount> sessionTable = getTable();
        sessionTable.putItem(userAccount);
    }

    public UserAccount getUserInfo(String id) {
        DynamoDbTable<UserAccount> userAccountDynamoDbTable = getTable();
        // Construct the key with partition and sort key
        Key key = Key.builder().partitionValue(id).build();
        return userAccountDynamoDbTable.getItem(key);
    }

    public UserAccount getUserInfobyEmail(String email) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":val", AttributeValue.builder().s(email).build());
        UserAccount userAccount = null;
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Users")
                .filterExpression("email = :val")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
        ScanResponse response = dynamoDbClient.scan(scanRequest);
        for (Map<String, AttributeValue> item : response.items()) {
            Set<String> keys = item.keySet();
            for (String key : keys) {
                userAccount=  new UserAccount(item.get("userId").s(),item.get("email").s(),item.get("firstName").s(),item.get("lastName").s());
            }

        }
        return userAccount;
    }

}
