package com.sashurman.uploadservice.service;

import com.sashurman.uploadservice.DTO.VideoUploadRequest;
import com.sashurman.uploadservice.DTO.event.VideoUploadedEvent;
import com.sashurman.uploadservice.exception.InvalidVideoException;
import com.sashurman.uploadservice.models.Video;
import com.sashurman.uploadservice.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {
    private static final long MAX_SIZE = 500L * 1024 * 1024;
    private static final Set<String> ALLOWED_FORMATS = Set.of(
            "video/mp4", "video/webm", "video/quicktime"
    );
    private final S3Client s3Client;
    @Value("${AWS.bucket-name}")
    private String bucketName;
    private final VideoRepository videoRepository;
    private final ApplicationEventPublisher eventPublisher;


    private void validateVideo(MultipartFile file){
        if (file.isEmpty()){
            throw new InvalidVideoException();
        }
        if (file.getSize() > MAX_SIZE){
            throw new InvalidVideoException("File too big");
        }
        if (!ALLOWED_FORMATS.contains(file.getContentType())){
            log.warn("Not supported format {}", file.getContentType());
            throw new InvalidVideoException("Not supported format");
        }
    }
    private String originalPathBuilder(MultipartFile file, UUID userId, UUID videoId){
        String contentType = file.getContentType();
        String ext = "";
        if (contentType != null && contentType.contains("/")) {
            ext = contentType.split("/")[1];
            if ("quicktime".equals(ext)) ext = "mov";}
        return String.format("raw/%s/%s/original.%s", userId, videoId, ext);
    }
    @Transactional
    public void uploadVideo(MultipartFile file, VideoUploadRequest request, UUID userId){
        validateVideo(file);
        Video video = new Video(
                userId,
                request.title(),
                file.getContentType()
        );
        videoRepository.save(video);
        String key = originalPathBuilder(file, userId, video.getId());
        try(InputStream inputStream = file.getInputStream()){
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.putObject(objectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        }
        catch (IOException e){
            throw new InvalidVideoException();
        }
        video.setOriginalPath(key);
        videoRepository.save(video);

        eventPublisher.publishEvent(new VideoUploadedEvent(
                UUID.randomUUID(),
                video.getId(),
                "video.uploaded",
                LocalDateTime.now(),
                userId,
                video.getOriginalPath(),
                video.getFileSizeBytes(),
                video.getFormat()
                ));
    }
}
