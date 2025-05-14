package ua.hudyma.Theater2025.model;

import java.io.Serializable;
import java.util.List;

public record SeatBatchRequest(
        List<SeatRequest> seats,
        String timeslot,
        Long movieId,
        Long hallId,
        Long userId) implements Serializable {
}
