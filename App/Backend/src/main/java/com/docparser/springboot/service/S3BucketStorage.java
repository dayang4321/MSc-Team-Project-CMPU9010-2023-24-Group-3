package com.docparser.springboot.service;

import com.docparser.springboot.model.S3StorageInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;


@Service
public class S3BucketStorage {

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${amazonProperties.region}")
    private String region;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }
    private PutObjectResponse  uploadFileToS3(String key, File file) {
        AwsCredentials credentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PutObjectResponse response = s3Client.putObject(putObjectRequest, file.toPath());
        System.out.println("File uploaded successfully. ETag: " + response.eTag());
        return response;
    }
    private String getUploadedObjectUrl(String fileName) {

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region,fileName);
    }

    public S3StorageInfo uploadFile(MultipartFile multipartFile) {
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            PutObjectResponse s3response = uploadFileToS3(fileName, file);
            String fileUrl = getUploadedObjectUrl(fileName);
            file.delete(); // Delete the temporary file after successful upload
            return new S3StorageInfo(s3response.eTag(), fileUrl);
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception instead of printing it
        }
        return null; // Return null if an error occurs
    }

}