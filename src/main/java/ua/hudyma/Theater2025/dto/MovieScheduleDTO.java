package ua.hudyma.Theater2025.dto;

import ua.hudyma.Theater2025.model.Schedule;

import java.util.List;

public record MovieScheduleDTO(Long movieId, List<Schedule> scheduleList){}
