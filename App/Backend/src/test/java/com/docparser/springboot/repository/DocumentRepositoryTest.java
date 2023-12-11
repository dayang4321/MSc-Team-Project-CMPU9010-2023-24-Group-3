package com.docparser.springboot.repository;

import com.docparser.springboot.model.DocumentInfo;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentRepositoryTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbenhancedClient;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private DocumentRepository documentRepository;

    @Test
    void save() {
        DynamoDbTable documentInfoTable = mock(DynamoDbTable.class);
        when(dynamoDbenhancedClient.table(eq("DocumentInfo"), any())).thenReturn(documentInfoTable);

        DocumentInfo documentInfo = new DocumentInfo();
        documentRepository.save(documentInfo);

        verify(documentInfoTable, times(1)).putItem(documentInfo);
    }

    @Test
    void getDocumentInfo() {
        DynamoDbTable documentInfoTable = mock(DynamoDbTable.class);
        when(dynamoDbenhancedClient.table(eq("DocumentInfo"), any())).thenReturn(documentInfoTable);

        DocumentInfo expectedDocumentInfo = new DocumentInfo();
        when(documentInfoTable.getItem(any(Key.class))).thenReturn(expectedDocumentInfo);

        String documentId = "testDocumentId";
        Optional<DocumentInfo> retrievedDocumentInfo = documentRepository.getDocumentInfo(documentId);

        verify(documentInfoTable, times(1)).getItem(any(Key.class));
        // Add assertions for the retrievedDocumentInfo
    }

    @Test
    void getDocumentsExpired() {
        // Mock DynamoDbClient
        ScanResponse scanResponse = ScanResponse.builder().items(new HashMap<>())
                .build();
        when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(scanResponse);

        // Call the method
        Map<String, Set<String>> documentsExpired = documentRepository.getDocumentsExpired();

        // Verify the interaction with DynamoDbClient
        verify(dynamoDbClient, times(1)).scan(any(ScanRequest.class));
        // Add assertions for documentsExpired
    }

    @Test
    void deleteSingleDocument() {
        DynamoDbTable documentInfoTable = mock(DynamoDbTable.class);
        when(dynamoDbenhancedClient.table(eq("DocumentInfo"), any())).thenReturn(documentInfoTable);

        // Call the method
        String documentId = "testDocumentId";
        documentRepository.deleteSingleDocument(documentId);

        // Verify the interaction with DynamoDbTable
        verify(documentInfoTable, times(1)).deleteItem(any(Key.class));
    }

    @Test
    void getDocumentKeys() {
        // Mock DynamoDbClient
        BatchGetItemResponse batchGetItemResponse = BatchGetItemResponse.builder()
                .responses(Collections.singletonMap("DocumentInfo", Collections.emptyList()))
                .build();
        when(dynamoDbClient.batchGetItem(any(BatchGetItemRequest.class))).thenReturn(batchGetItemResponse);

        // Call the method
        HashSet<String> val = new HashSet<String>();
        val.add("testDocumentId");
        Set<String> documentKeys = documentRepository.getDocumentKeys(val);

        // Verify the interaction with DynamoDbClient
        verify(dynamoDbClient, times(1)).batchGetItem(any(BatchGetItemRequest.class));
        // Add assertions for documentKeys
    }

    @Test
    void deleteDocument() {
        // Mock DynamoDbClient
        BatchWriteItemResponse batchWriteItemResponse = BatchWriteItemResponse.builder().build();
        when(dynamoDbClient.batchWriteItem(any(BatchWriteItemRequest.class))).thenReturn(batchWriteItemResponse);

        // Call the method
        Set<String> documentIDs = Collections.singleton("testDocumentId");
        documentRepository.deleteDocument(documentIDs);

        // Verify the interaction with DynamoDbClient
        verify(dynamoDbClient, times(1)).batchWriteItem(any(BatchWriteItemRequest.class));
    }
}
