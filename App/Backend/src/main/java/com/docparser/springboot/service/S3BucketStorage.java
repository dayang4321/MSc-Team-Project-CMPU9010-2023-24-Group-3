package com.docparser.springboot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3BucketStorage {
    Logger logger = LoggerFactory.getLogger(S3BucketStorage.class);

    // Injecting the S3 bucket name from application properties
    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    // Autowiring the Amazon S3 client for interacting with S3 service
    @Autowired
    private S3Client s3Client;

    // Autowiring the S3Presigner for generating pre-signed URLs
    @Autowired
    private S3Presigner s3Presigner;

    // Method to upload a file to S3
    public PutObjectResponse uploadFileToS3(String key, File file) throws IOException {
        // Creating a request object to upload a file
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        // Uploading the file to S3 and getting the response
        PutObjectResponse response = s3Client.putObject(putObjectRequest, file.toPath());
        // Logging the response
        logger.info("modified file successfully uploaded to s3" + response.toString());
        return response;
    }

    // Method to get a pre-signed URL for an uploaded object in S3
    public String getUploadedObjectUrl(String fileName, String versionId) {
        // Creating a request object to get an object
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .versionId(versionId)
                .build();
        // Creating a pre-sign request with a duration for the URL
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(180))
                .getObjectRequest(getObjectRequest)
                .build();
        // Generating the pre-signed URL
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        // Logging the URL
        logger.info("successfully obtained presigned URL" + presignedGetObjectRequest.url().toString());
        return presignedGetObjectRequest.url().toString();
    }

    // Method to get an InputStream of a file from S3
    public InputStream getFileStreamFromS3(String key, String versionId) {
        // Creating a request object to get an object
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .versionId(versionId)
                .build();
        // Getting the object as a byte array
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        // Converting byte array to InputStream
        InputStream inputStream = new ByteArrayInputStream(objectBytes.asByteArray());
        return inputStream;
    }

    // Method to delete multiple objects from an S3 bucket
    public void deleteBucketObjects(List<String> documentKeys) {
        logger.info("deleting objects from s3 bucket");
        // Check if there are objects to delete
        if (!documentKeys.isEmpty()) {
            // Preparing a list of ObjectIdentifiers for the objects to delete
            ArrayList<ObjectIdentifier> keys = new ArrayList<>();
            for (String key : documentKeys) {
                keys.add(ObjectIdentifier.builder().key(key).build());
            }
            // Creating a delete request
            Delete del = Delete.builder()
                    .objects(keys)
                    .build();

            try {
                // Sending the delete request to S3
                DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(del)
                        .build();

                s3Client.deleteObjects(multiObjectDeleteRequest);
                System.out.println("Multiple objects are deleted!");

            } catch (S3Exception e) {
                throw new RuntimeException("Error while deleting objects from S3 bucket");
            }
        }
    }
}
