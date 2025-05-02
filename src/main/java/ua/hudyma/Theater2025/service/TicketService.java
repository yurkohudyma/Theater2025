package ua.hudyma.Theater2025.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.Theater2025.repository.TicketRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class TicketService {

    TicketRepository ticketRepository;

    public void addNewTicket(Object o) {
        //todo implement
    }

    public LocalDateTime convertTimeSlotToLocalDateTime(String timeSlot) {
        LocalTime time = LocalTime.parse(timeSlot);
        return LocalDate.now().atTime(time);
    }
}