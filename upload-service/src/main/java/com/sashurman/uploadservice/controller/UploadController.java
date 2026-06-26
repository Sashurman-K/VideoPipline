package com.sashurman.uploadservice.controller;

import com.sashurman.uploadservice.DTO.VideoUploadRequest;
import com.sashurman.uploadservice.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UploadController {
    final UploadService uploadService;
    @PostMapping("/api/v1/videos/upload")
    public ResponseEntity<Void> uploadVideo(@RequestHeader("X-User-Id") String userId,
                                            @RequestParam("file") MultipartFile file,
                                            @ModelAttribute VideoUploadRequest request){
        uploadService.uploadVideo(file, request, UUID.fromString(userId));
        return ResponseEntity.ok().build();
    }
}
