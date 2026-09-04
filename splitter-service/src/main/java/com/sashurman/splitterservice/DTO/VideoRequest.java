package com.sashurman.splitterservice.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record VideoRequest(
        UUID eventId,
        UUID videoId,
        String eventType,
        LocalDateTime timestamp,
        UUID userId,
        String originalPath,
        Long fileSizeBytes,
        String format
) {
}
