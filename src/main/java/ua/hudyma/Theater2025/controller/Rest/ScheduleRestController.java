package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.model.Schedule;
import ua.hudyma.Theater2025.repository.ScheduleRepository;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleRestController {

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleRestController(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    @GetMapping("{id}")
    public Schedule getById(@PathVariable("id") Integer id) {
        return scheduleRepository.findById(id).orElseThrow();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addSchedule(@RequestBody Schedule schedule) {
        System.out.println("...adding schedule " + schedule.getTimeSlot());
        scheduleRepository.save(schedule);
    }

    @PostMapping("/addAll")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAll(@RequestBody Schedule[] schedules) {
        Arrays.stream(schedules).forEach(this::addSchedule);
    }


}
