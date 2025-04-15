package ua.hudyma.Theater2025.dto;

import ua.hudyma.Theater2025.model.Hall;

public record HallDTO(
        Integer id,
        Integer rowz,
        Integer seats,
        String name,
        Double seatPrice
) {

    public static HallDTO from (Hall hall){
        return new HallDTO(
                hall.getId(),
                hall.getRowz(),
                hall.getSeats(),
                hall.getName(),
                hall.getSeatPrice());
    }
}
