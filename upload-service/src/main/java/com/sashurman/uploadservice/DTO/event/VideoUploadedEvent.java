package com.sashurman.uploadservice.DTO.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VideoUploadedEvent(
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
