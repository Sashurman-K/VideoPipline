package com.sashurman.splitterservice.DTO;

import com.sashurman.splitterservice.enums.SplitMode;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
//TODO подумать про интерфейс для этого рекорда
public record VideoRequest (
        UUID eventId,
        UUID videoId,
        LocalDateTime timestamp,
        UUID userId,
        String originalPath,

        SplitMode mode,
        @Nullable Integer segmentCount,
        @Nullable Double segmentDuration,
        @Nullable List<TimeSegment> segments
) {
}
