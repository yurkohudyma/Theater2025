package ua.hudyma.Theater2025.dto;

import ua.hudyma.Theater2025.model.Seat;

public record SeatDTO(
        Long id,
        int rowNumber,
        int seatNumber,
        boolean isOccupied,
        Double seatPrice,
        Integer hallId
) {

    public static SeatDTO from (Seat seat){
        return new SeatDTO(
                seat.getId(),
                seat.getRowNumber(),
                seat.getSeatNumber(),
                seat.isOccupied(),
                seat.getPrice(),
                seat.getHall().getId()

        );
    }
}