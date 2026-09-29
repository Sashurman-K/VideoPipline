package com.sashurman.splitterservice.service;

import com.sashurman.common.exception.InternalServerException;
import com.sashurman.splitterservice.DTO.VideoRequest;
import com.sashurman.splitterservice.utils.FfmpegNativeProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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

@Service
public class SplitterService {
    private final Semaphore semaphore = new Semaphore(10);
    private final S3Service s3Service;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FfmpegNativeProducer nativeProducer;

    public SplitterService(FfmpegNativeProducer nativeProducer,
                           KafkaTemplate<String, Object> kafkaTemplate,
                           S3Service s3Service) {
        this.kafkaTemplate = kafkaTemplate;
        this.s3Service = s3Service;
        this.nativeProducer = nativeProducer;
    }

    public void splitByCount(VideoRequest request) {
        File rawVideo = s3Service.downloadToTempFile(request.originalPath());
        try {
            Path videoTempDir = Files.createDirectories(
                    Path.of(System.getProperty("java.io.tmpdir"),
                            "splitter",
                            request.videoId().toString()));
            List<File> chunks = nativeProducer.splitVideo(rawVideo, videoTempDir.toFile(), request.segmentCount(),
                    getVideoDurationSeconds(rawVideo));
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                IntStream.range(0, chunks.size()).forEach(i -> {
                    File chunk = chunks.get(i);

                    executor.execute(() -> {
                        try {
                            semaphore.acquire();

                            double chunkDuration = getVideoDurationSeconds(chunk);

                            ChunkUploadTask task = new ChunkUploadTask(
                                    chunk,
                                    request.format(),
                                    chunkDuration,
                                    chunks.size(),
                                    i,
                                    request.videoId().toString(),
                                    s3Service,
                                    kafkaTemplate
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
            }
        } catch (Exception ex) {
            throw new InternalServerException(ex.getMessage());
        }

    }

    public double getVideoDurationSeconds(File videoFile) throws IOException, InterruptedException {
        List<String> command = List.of(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                videoFile.getAbsolutePath()
        );

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String output = reader.readLine();
            int exitCode = process.waitFor();

            if (exitCode == 0 && output != null && !output.isBlank()) {
                return Double.parseDouble(output.trim());
            }
        }

        throw new RuntimeException("Не удалось определить длительность видео: " + videoFile.getName());
    }
}
