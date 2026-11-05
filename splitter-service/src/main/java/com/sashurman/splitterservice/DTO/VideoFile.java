package com.sashurman.splitterservice.DTO;

import com.sashurman.splitterservice.utils.FfmpegNativeProducer;
import jakarta.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public record VideoFile(
        File file,
        double duration,
        String extension,
        double frameRate,
        int width,
        int height,
        long size,
        @Nullable String s3Key
){

}
