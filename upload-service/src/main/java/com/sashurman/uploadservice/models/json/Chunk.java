package com.sashurman.uploadservice.models.json;

import java.io.Serializable;

public record Chunk (
        String path,
        double startSec,
        double endSec,
        Long sizeBytes
)
{
}
