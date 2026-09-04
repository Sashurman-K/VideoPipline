package com.sashurman.splitterservice.DTO;

import java.util.List;
import java.util.stream.IntStream;

public record TimeSegment(
        double startSeconds,
        double endSeconds
) {
    public static List<TimeSegment> generateSegments(double totalDuration, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Количество сегментов должно быть > 0");
        }

        double step = totalDuration / count;

        return IntStream.range(0, count)
                .mapToObj(i -> new TimeSegment(
                        i * step,
                        (i == count - 1) ? totalDuration : (i + 1) * step
                ))
                .toList();
    }
}
