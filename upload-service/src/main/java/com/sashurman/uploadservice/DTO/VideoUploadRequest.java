package com.sashurman.uploadservice.DTO;

import org.springframework.web.multipart.MultipartFile;

public record VideoUploadRequest(
        String title,
        String description
) {
}
