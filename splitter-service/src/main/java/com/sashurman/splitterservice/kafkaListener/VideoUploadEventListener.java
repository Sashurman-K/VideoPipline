package com.sashurman.splitterservice.kafkaListener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VideoUploadEventListener {

    @KafkaListener(
            topics = "video-upload-event-topic",
            groupId = "splitter-group"
    )
    public void handleVideoUploadEvent(String message) {
        log.info("Получено сообщение из Kafka: {}", message);

    }
}