package com.sashurman.splitterservice.utils;

import com.sashurman.splitterservice.DTO.TimeSegment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class FfmpegNativeProducer {

    private FfmpegNativeProducer(){
        throw new UnsupportedOperationException("Cannot create an instance");
    }

    public static List<File> splitVideo(File inputFile, File outputDir, int segmentDurationSeconds)
            throws IOException, InterruptedException {

        // Шаблон имени чанков: output_001.mp4, output_002.mp4
        String outputPattern = new File(outputDir, "chunk_%03d.mp4").getAbsolutePath();

        // Собираем команду CLI
        List<String> command = List.of(
                "ffmpeg",
                "-i", inputFile.getAbsolutePath(),
                "-c", "copy",
                "-map", "0",
                "-segment_time", String.valueOf(segmentDurationSeconds),
                "-f", "segment",
                "-reset_timestamps", "1",
                outputPattern
        );

        ProcessBuilder processBuilder = new ProcessBuilder(command);


        processBuilder.redirectErrorStream(true);

        processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);

        Process process = processBuilder.start();


        boolean finished = process.waitFor(5, TimeUnit.MINUTES);

        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("FFmpeg превысил лимит времени выполнения");
        }

        if (process.exitValue() != 0) {
            throw new RuntimeException("FFmpeg завершился с ошибкой, код: " + process.exitValue());
        }
        // Собираем и сортируем созданные файлы
        File[] files = outputDir.listFiles((dir, name) -> name.startsWith("chunk_") && name.endsWith(".mp4"));
        if (files == null) {
            return List.of();
        }

        Arrays.sort(files, Comparator.comparing(File::getName));
        return List.of(files);
    }
    public static List<File> splitVideo(File inputFile, File outputDir, List<TimeSegment> segments)
            throws IOException, InterruptedException {

        List<File> outputFiles = new ArrayList<>();

        for (int i = 0; i < segments.size(); i++) {
            TimeSegment segment = segments.get(i);
            File outputFile = new File(outputDir, String.format("segment_%03d.mp4", i + 1));


            List<String> command = List.of(
                    "ffmpeg",
                    "-ss", String.valueOf(segment.startSeconds()),
                    "-to", String.valueOf(segment.endSeconds()),
                    "-i", inputFile.getAbsolutePath(),
                    "-c", "copy",
                    "-avoid_negative_ts", "make_zero",
                    outputFile.getAbsolutePath()
            );

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            Process process = processBuilder.start();
            boolean finished = process.waitFor(2, TimeUnit.MINUTES);

            if (!finished || process.exitValue() != 0) {
                process.destroyForcibly();
                throw new RuntimeException("Ошибка при нарезке интервала: " + segment);
            }

            outputFiles.add(outputFile);
        }

        return outputFiles;
    }

    public static List<File>  splitVideo(File inputFile, File outputDir, int quantity, double durationFUllVideo) throws IOException, InterruptedException {
        List<TimeSegment> segments = TimeSegment.generateSegments(durationFUllVideo, quantity);
        return splitVideo(inputFile, outputDir, segments);

    }
}
