package ua.hudyma.Theater2025.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.Theater2025.model.Movie;
import ua.hudyma.Theater2025.model.Schedule;
import ua.hudyma.Theater2025.repository.MovieRepository;
import ua.hudyma.Theater2025.repository.ScheduleRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ScheduleService {

    private final MovieRepository movieRepository;
    private final ScheduleRepository scheduleRepository;

    public Map<Movie, List<String>> getMovieScheduleMap(List<Movie> movieList) {
        return movieList.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        movie -> {
                            Schedule schedule = movie.getSchedule();
                            if (schedule == null) {
                                return List.of(); // або Collections.emptyList()
                            }
                            return List.of(
                                    schedule.getTimeSlot(),
                                    schedule.getTimeSlot2(),
                                    schedule.getTimeSlot3()
                            );
                        }
                ));
    }


}
