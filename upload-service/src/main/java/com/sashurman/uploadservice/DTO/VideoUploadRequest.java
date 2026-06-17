package com.sashurman.uploadservice.DTO;

import org.springframework.web.multipart.MultipartFile;

public record VideoUploadRequest(
        MultipartFile file,
        String title,
        String description
) {
}
