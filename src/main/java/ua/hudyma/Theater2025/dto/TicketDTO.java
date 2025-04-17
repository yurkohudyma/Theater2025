package ua.hudyma.Theater2025.dto;

import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.model.Ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TicketDTO(Long id, Double value, LocalDateTime purchasedOn, LocalDateTime scheduledOn, Integer roww, Integer seat, TicketStatus ticketStatus, Long userId, Integer hallId, Long movieId) {
    public static TicketDTO from (Ticket ticket){
        return new TicketDTO(
                ticket.getId(),
                ticket.getValue(),
                ticket.getPurchasedOn(),
                ticket.getScheduledOn(),
                ticket.getRoww(),
                ticket.getSeat(),
                ticket.getTicketStatus(),
         ticket.getUser().getId(),
         ticket.getHall().getId(),
                ticket.getMovie().getId());

    }
}
