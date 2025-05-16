package ua.hudyma.Theater2025.dto;

import java.time.LocalDateTime;

public record EmailMovieDTO (
        String movieTitle,
        LocalDateTime dateTime,
        int seatNumber,
        int rowNumber,
        double price,
        byte[] qrBase64) {}
