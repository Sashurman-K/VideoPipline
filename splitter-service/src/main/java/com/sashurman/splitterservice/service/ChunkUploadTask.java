package com.sashurman.splitterservice.service;

import com.sashurman.splitterservice.DTO.KafkaProcessingRequest;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.File;
import java.time.Instant;


public class ChunkUploadTask implements Runnable {

    private static final String CHUNK_KEY_PATTERN = "videos/%s/chunks/chunk_%04d.%s";
    private final File chunk;
    private final String chunkExtension;
    private final int totalChunks;
    private final int chunkIndex;
    private final String videoId;
    private final S3Service s3Service;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String kafkaTopicName;


    public ChunkUploadTask(File chunk,
                           String chunkExtension,
                           int totalChunks,
                           int chunkIndex,
                           String videoId,
                           S3Service s3Service,
                           KafkaTemplate<String, Object> kafkaTemplate,
                           String kafkaTopicName) {
        this.chunk = chunk;
        this.chunkExtension = chunkExtension;
        this.totalChunks = totalChunks;
        this.chunkIndex = chunkIndex;
        this.videoId = videoId;
        this.s3Service = s3Service;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicName = kafkaTopicName;
    }

    @Override
    public void run() {
        String s3key = generateChunkKey(videoId, chunkIndex, chunkExtension);
        s3Service.uploadFile(chunk, s3key);

        kafkaTemplate.send(kafkaTopicName, new KafkaProcessingRequest(
                videoId,
                chunkIndex,
                totalChunks,
                s3key,
                Instant.now()
        ));
    }

    private String generateChunkKey(String videoId, int chunkIndex, String extension) {
        String cleanExtension = extension.startsWith(".") ? extension.substring(1) : extension;
        return String.format(CHUNK_KEY_PATTERN, videoId, chunkIndex, cleanExtension);
    }
}
