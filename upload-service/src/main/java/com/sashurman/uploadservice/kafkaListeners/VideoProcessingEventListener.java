package com.sashurman.uploadservice.kafkaListeners;

import com.sashurman.common.exception.InternalServerException;

import com.sashurman.uploadservice.service.UploadService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@AllArgsConstructor
public class VideoProcessingEventListener {
    private final ObjectMapper objectMapper;
    private final UploadService uploadService;


    @KafkaListener(
            topics = "${pipeline.kafka.processing-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleVideoUploadEvent(String message) {
        log.info("Получено сообщение из Kafka: {}", message);
        try{

        }
        catch (Exception ex){
            throw new InternalServerException("Error map request");
        }
    }
}