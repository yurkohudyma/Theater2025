package ua.hudyma.Theater2025.model;

import java.util.List;

public record SeatBatchRequest(
        List<SeatRequest> seats,
        String timeslot,
        Long movieId,
        Long hallId) {
}
