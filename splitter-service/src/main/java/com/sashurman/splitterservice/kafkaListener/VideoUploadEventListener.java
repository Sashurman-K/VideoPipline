package com.sashurman.splitterservice.kafkaListener;

import com.sashurman.common.exception.InternalServerException;
import com.sashurman.splitterservice.DTO.VideoRequest;
import com.sashurman.splitterservice.service.SplitterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@AllArgsConstructor
public class VideoUploadEventListener {
    private final ObjectMapper objectMapper;
    private final SplitterService splitterService;
    @KafkaListener(
            topics = "${pipeline.kafka.upload-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleVideoUploadEvent(String message) {
        log.info("Получено сообщение из Kafka: {}", message);
        try{
            var request = objectMapper.readValue(message, VideoRequest.class);
            splitterService.splitProducer(request);
        }
        catch (Exception ex){
            throw new InternalServerException("Error map request");
        }
    }
}