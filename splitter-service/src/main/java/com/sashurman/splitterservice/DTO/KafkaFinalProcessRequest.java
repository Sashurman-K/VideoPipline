package com.sashurman.splitterservice.DTO;

import java.time.Instant;
import java.util.UUID;

public record KafkaFinalProcessRequest(
        UUID videoId,
        boolean isSuccess,
        int totalChunks,
        Instant timestamp
) {
}
