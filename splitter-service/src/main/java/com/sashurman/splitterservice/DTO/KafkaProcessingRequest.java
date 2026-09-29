package com.sashurman.splitterservice.DTO;

import java.time.Instant;

public record KafkaProcessingRequest(
        String videoId,
        int chunkIndex,
        int totalChunks,
        String s3Key,
        double durationSeconds,
        Instant timestamp
) {
}

