package com.sashurman.splitterservice.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FFmpegFileInfoInput(
        Format format,
        List<Stream> streams
){
    @JsonIgnoreProperties(ignoreUnknown = true)

    public record Format(
        String duration,
        String size,
        @JsonProperty("format_name") String formatName
    ){}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Stream(
            int index,
            @JsonProperty("codec_type") String codecType,
            @JsonProperty("codec_name") String codecName,
            int width,
            int height,
            @JsonProperty("avg_frame_rate") String averageFrameRate
    ){}
}

