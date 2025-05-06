package ua.hudyma.Theater2025.dto;

import ua.hudyma.Theater2025.model.*;

public class TicketMapper {

    public static Ticket toEntity(TicketDTO dto, User user, Hall hall, Movie movie) {
        return Ticket.builder()
                .id(dto.id())
                .value(dto.value())
                .purchasedOn(dto.purchasedOn())
                .scheduledOn(dto.scheduledOn())
                .roww(dto.roww())
                .seat(dto.seat())
                .ticketStatus(dto.ticketStatus())
                .user(user)
                .hall(hall)
                .movie(movie)
                .build();
    }
}

