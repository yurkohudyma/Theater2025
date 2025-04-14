package ua.hudyma.Theater2025.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class TicketService {

    public LocalDateTime convertTimeSlotToLocalDateTime(String timeSlot) {
        LocalTime time = LocalTime.parse(timeSlot);
        return LocalDate.now().atTime(time);
    }
}