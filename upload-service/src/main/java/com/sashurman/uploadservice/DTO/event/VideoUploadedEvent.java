package com.sashurman.uploadservice.DTO.event;

import java.util.UUID;

public record VideoUploadedEvent(
        UUID eventId,

) {
}
