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


@Service
public class S3BucketStorage {
    Logger logger = LoggerFactory.getLogger(S3BucketStorage.class);
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private S3Presigner s3Presigner;
  


    public PutObjectResponse uploadFileToS3(String key, File file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        PutObjectResponse response = s3Client.putObject(putObjectRequest, file.toPath());
        logger.info("modified file successfully uploaded to s3"+response.toString());
        return response;
    }

    public String getUploadedObjectUrl(String fileName, String versionId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .versionId(versionId)
                .build();
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(180))
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        logger.info("successfully obtained presigned URL"+presignedGetObjectRequest.url().toString());
        return presignedGetObjectRequest.url().toString();
    }

    public InputStream getFileStreamFromS3(String key, String versionId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .versionId(versionId)
                .build();
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        byte[] data = objectBytes.asByteArray();
        InputStream inputStream = new ByteArrayInputStream(data);
        return inputStream;
    }

}