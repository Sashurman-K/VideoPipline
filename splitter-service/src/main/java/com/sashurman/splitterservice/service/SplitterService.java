package com.sashurman.splitterservice.service;

import com.sashurman.common.exception.InternalServerException;
import com.sashurman.common.exception.InvalidVideoException;
import com.sashurman.splitterservice.DTO.KafkaFinalProcessRequest;
import com.sashurman.splitterservice.DTO.VideoFile;
import com.sashurman.splitterservice.DTO.VideoRequest;
import com.sashurman.splitterservice.utils.FfmpegNativeProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;
@Slf4j
@Service
public class SplitterService {
    private final Semaphore semaphore = new Semaphore(10);
    private final S3Service s3Service;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FfmpegNativeProducer nativeProducer;
    @Value("${pipeline.kafka.processing-topic}")
    private String processingTopic;
    @Value("${pipeline.kafka.final-topic}")
    private String finalTopic;

    public SplitterService(FfmpegNativeProducer nativeProducer,
                           KafkaTemplate<String, Object> kafkaTemplate,
                           S3Service s3Service) {
        this.kafkaTemplate = kafkaTemplate;
        this.s3Service = s3Service;
        this.nativeProducer = nativeProducer;
    }

    public void splitProducer(VideoRequest request) {
        boolean isSuccess = false;
        File file = s3Service.downloadToTempFile(request.originalPath());
        var rawVideo = new VideoFile(
                file,
                request.originalPath());
        try {
            Path videoTempDir = Files.createDirectories(
                    Path.of(System.getProperty("java.io.tmpdir"),
                            "splitter",
                            request.videoId().toString()));
            List<File> chunks = nativeProducer.splitVideo(rawVideo, videoTempDir.toFile(),
                    switch (request.mode()) {
                        case BY_COUNT -> request.segmentCount();
                        case BY_DURATION -> request.segmentDuration();
                        case BY_TIMESTAMPS -> request.segments();
                        default -> throw new InvalidVideoException();
                    });
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                IntStream.range(0, chunks.size()).forEach(i -> {
                    var chunk = chunks.get(i);

                    executor.execute(() -> {
                        try {
                            semaphore.acquire();

                            ChunkUploadTask task = new ChunkUploadTask(
                                    chunk,
                                    FfmpegNativeProducer.getFileExtension(chunk.getName()),
                                    chunks.size(),
                                    i,
                                    request.videoId().toString(),
                                    s3Service,
                                    kafkaTemplate,
                                    processingTopic
                            );

                            task.run();

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new InternalServerException("Поток был прерван во время ожидания семафора");
                        } catch (Exception e) {
                            throw new InternalServerException("Ошибка при обработке чанка: " + chunk.getName());
                        } finally {
                            semaphore.release();
                        }
                    });
                });
                isSuccess = true;
            }
        } catch (Exception ex) {
            throw new InternalServerException(ex.getMessage());
        }
        finally {
            if (file != null && !file.delete()) log.error("Error delete file {}", file.getAbsolutePath());
            kafkaTemplate.send(finalTopic,
                    new KafkaFinalProcessRequest(

                    ));
        }
    }


}
