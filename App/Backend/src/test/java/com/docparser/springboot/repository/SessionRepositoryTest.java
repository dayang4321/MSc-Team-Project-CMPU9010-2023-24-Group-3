package com.docparser.springboot.repository;

import com.docparser.springboot.model.SessionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionRepositoryTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbenhancedClient;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private SessionRepository sessionRepository;

    @Test
    void save() {
        DynamoDbTable sessionTable = mock(DynamoDbTable.class);
        when(dynamoDbenhancedClient.table(eq("SessionInfo"), any())).thenReturn(sessionTable);
        SessionInfo sessionInfo = new SessionInfo();
        sessionRepository.save(sessionInfo);

        verify(sessionTable, times(1)).putItem(sessionInfo);
    }

    @Test
    void getSessionInfo() {
        DynamoDbTable sessionTable = mock(DynamoDbTable.class);
        when(dynamoDbenhancedClient.table(eq("SessionInfo"), any())).thenReturn(sessionTable);

        SessionInfo expectedSessionInfo = new SessionInfo();
        when(sessionTable.getItem(any(Key.class))).thenReturn(expectedSessionInfo);

        String sessionId = "testSessionId";
        SessionInfo retrievedSessionInfo = sessionRepository.getSessionInfo(sessionId);

        verify(sessionTable, times(1)).getItem(any(Key.class));
        // Add assertions for the retrievedSessionInfo
    }

    @Test
    void deleteUserSession() {
        // Mock DynamoDbClient
        DeleteItemResponse deleteItemResponse = DeleteItemResponse.builder().build();
        when(dynamoDbClient.deleteItem(any(DeleteItemRequest.class))).thenReturn(deleteItemResponse);

        // Call the method
        String userSessionId = "testUserSessionId";
        sessionRepository.deleteUserSession(userSessionId);

        // Verify the interaction with DynamoDbClient
        verify(dynamoDbClient, times(1)).deleteItem(any(DeleteItemRequest.class));
    }
}
