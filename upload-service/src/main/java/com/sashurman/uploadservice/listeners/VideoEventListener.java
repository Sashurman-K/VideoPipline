package com.sashurman.uploadservice.listeners;

import com.sashurman.uploadservice.DTO.event.VideoUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoEventListener {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVideoUploadedEvent(VideoUploadedEvent event) {
        log.info("Транзакция успешна. Отправляем ивент в Кафку для видео: {}", event.videoId());
        String jsonPayload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("product-created-events-topic", jsonPayload);

    }

}
