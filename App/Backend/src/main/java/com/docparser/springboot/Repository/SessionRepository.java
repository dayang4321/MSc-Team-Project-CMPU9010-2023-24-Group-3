package com.docparser.springboot.Repository;

import com.docparser.springboot.model.SessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class SessionRepository {
    @Autowired
    private DynamoDbEnhancedClient dynamoDbenhancedClient;

    private DynamoDbTable<SessionInfo> getTable() {
        // Create a tablescheme to scan our bean class order
        return dynamoDbenhancedClient.table("SessionInfo",
                TableSchema.fromBean(SessionInfo.class));
    }

    public void save(SessionInfo sessionInfo) {
        DynamoDbTable<SessionInfo> sessionTable = getTable();
        sessionTable.putItem(sessionInfo);
    }

    public SessionInfo getSessionInfo(String sessionID) {
        DynamoDbTable<SessionInfo> sessionTable = getTable();
        // Construct the key with partition and sort key
        Key key = Key.builder().partitionValue(sessionID).build();
        return sessionTable.getItem(key);
    }
}
