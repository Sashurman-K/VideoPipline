package com.sashurman.splitterservice.service;

import com.sashurman.common.exception.InternalServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class S3Service {
    private final S3Client s3Client;
    @Value("${AWS.proccessed-bucket-name}")
    private String processingBucketName;
    @Value("${AWS.raw-bucket-name")
    private String rawBucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public File downloadToTempFile(String s3Key) {
        String extension = extractExtension(s3Key);
        try {
            Path tempFilePath = Files.createTempFile("raw_video_", extension);
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(rawBucketName)
                    .key(s3Key)
                    .build();
            s3Client.getObject(getObjectRequest, tempFilePath);
            File file = tempFilePath.toFile();
            file.deleteOnExit();
            return file;
        } catch (Exception ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    public void uploadChunk(File chunk, String path) {
        try (InputStream stream = new FileInputStream(chunk)) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(processingBucketName)
                    .key(path)
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(stream, chunk.length()));
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    private String extractExtension(String s3Key) {
        int lastDotIndex = s3Key.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < s3Key.length() - 1) {
            return s3Key.substring(lastDotIndex);
        }
        return ".mp4";
    }
}
