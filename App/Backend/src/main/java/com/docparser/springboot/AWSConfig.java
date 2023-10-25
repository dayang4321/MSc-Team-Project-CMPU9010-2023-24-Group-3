package com.docparser.springboot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AWSConfig {

    @Value("${amazonProperties.accessKey}")
    private String accessKey;

    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @Value("${amazonProperties.region}")
    private String region;


    private StaticCredentialsProvider getAccessCredentials (){
        AwsCredentials credentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);
        return  StaticCredentialsProvider.create(credentials);
    }
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(getAccessCredentials())
                .build();
    }

    @Bean
    public S3Presigner S3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(getAccessCredentials())
                .build();
    }
}
