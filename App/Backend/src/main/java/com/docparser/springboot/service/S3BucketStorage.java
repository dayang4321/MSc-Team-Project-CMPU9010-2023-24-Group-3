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

import java.io.*;
import java.util.Date;
import java.util.Objects;

@Service
public class S3BucketStorage {

    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.region}")
    private String region;

    @Autowired
    private S3Client s3Client;

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

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }

    public S3StorageInfo uploadFile(MultipartFile multipartFile) throws IOException {

        File file = convertMultiPartToFile(multipartFile);
        String fileName = generateFileName(multipartFile);
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
            e.printStackTrace(); // Handle the exception
        }
        // Write the data to a local file.
        return new FileSystemResource(tempFile);
    }

}