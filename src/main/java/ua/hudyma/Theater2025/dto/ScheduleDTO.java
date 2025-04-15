package ua.hudyma.Theater2025.dto;

import ua.hudyma.Theater2025.model.Schedule;

public record ScheduleDTO(Integer id, String timeSlot) {

    public static ScheduleDTO from (Schedule schedule){
        return new ScheduleDTO(
                schedule.getId(),
                schedule.getTimeSlot()
        );
    }
}
