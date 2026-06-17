package com.sashurman.uploadservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;
import java.net.URISyntaxException;
@Slf4j
@Configuration
public class S3Config {
    @Value("${AWS.access-key}")
    private String accessKey;
    @Value("${AWS.secret-key}")
    private String secretKey;
    @Value("${AWS.region}")
    private String region;
    @Value("${AWS.bucket-name}")
    private String bucketName;
    @Value("${AWS.endpoint}")
    private String endpoint;
    @Bean
    public S3Client s3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();
        URI uri = null;
        try {
            uri = new URI(endpoint);
        }
        catch (Exception ex){
            throw new RuntimeException("failed to create uri");
        }
        S3Client s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(uri)
                .serviceConfiguration(s3Configuration)
                .build();
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());

            log.info("Bucket '{}' already exists.", bucketName);

        } catch (NoSuchBucketException e) {
            log.info("Bucket '{}' not found. initializing creation...", bucketName);
            createS3Bucket(s3Client);

        } catch (S3Exception e) {
            log.error("Error AWS S3 on bucket check (Status code: {}): {}", e.statusCode(), e.getMessage());
            throw e;
        }
        return s3Client;

    }
    private void createS3Bucket(S3Client s3Client) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            log.info("Bucket '{}' success created.", bucketName);
        } catch (S3Exception e) {
            log.error("Failed to create a bucket '{}': {}", bucketName, e.getMessage());
            throw e;
        }
    }
}
