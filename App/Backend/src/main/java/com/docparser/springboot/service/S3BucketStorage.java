package com.docparser.springboot.service;

import com.docparser.springboot.model.S3StorageInfo;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.*;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Service
public class S3BucketStorage {
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private S3Presigner s3Presigner;

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
    private String generateFileName(File file) {
        return new Date().getTime() + "-" + file.getName().replace(" ", "_");
    }

    private PutObjectResponse uploadFileToS3(String key, File file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        PutObjectResponse response = s3Client.putObject(putObjectRequest, file.toPath());
        System.out.println("File uploaded successfully. ETag: " + response.eTag());
        return response;
    }

    private String getUploadedObjectUrl(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

    public S3StorageInfo uploadFile(MultipartFile multipartFile) throws IOException {
        File file = convertMultiPartToFile(multipartFile);
        String fileName = generateFileName(multipartFile);
        PutObjectResponse s3response = uploadFileToS3(fileName, file);
        String fileUrl = getUploadedObjectUrl(fileName);
        file.delete(); // Delete the temporary file after successful upload
        return new S3StorageInfo(s3response.eTag(), fileUrl, fileName);
    }
    public S3StorageInfo uploadFile(File file) throws IOException {
        String fileName = generateFileName(file);
        PutObjectResponse s3response = uploadFileToS3(fileName, file);
        String fileUrl = getUploadedObjectUrl(fileName);
        file.delete(); // Delete the temporary file after successful upload
        return new S3StorageInfo(s3response.eTag(), fileUrl, fileName);
    }

    public InputStream getFileStreamFromS3(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        byte[] data = objectBytes.asByteArray();
        InputStream inputStream = new ByteArrayInputStream(data);
        return inputStream;
    }

    public FileSystemResource download(String key) throws IOException {
        InputStream inputStream = getFileStreamFromS3(key);
        File tempFile = File.createTempFile("downloadedFile", ".docx");
        tempFile.deleteOnExit();
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            FileOutputStream out = new FileOutputStream(tempFile);
            document.write(out);
            out.close();
        } catch (IOException e) {
            throw new IOException(e); // Handle the exception
        }
        // Write the data to a local file.
        return new FileSystemResource(tempFile);
    }
}