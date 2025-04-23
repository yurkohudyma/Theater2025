package ua.hudyma.Theater2025.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.hudyma.Theater2025.constants.Genre;
import ua.hudyma.Theater2025.model.Hall;
import ua.hudyma.Theater2025.model.Movie;
import ua.hudyma.Theater2025.model.Schedule;
import ua.hudyma.Theater2025.repository.*;
import ua.hudyma.Theater2025.service.HallService;
import util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/admin")
//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Log4j2
public class AdminController {
    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final HallService hallService;
    private final ScheduleRepository scheduleRepository;
    @Value("${uploadConfig}")
    private String uploadConfig;
    @Value("${basePathConfig}")
    private String basePathConfig;

    @GetMapping
    public String getEverything(Model model, Principal principal) {
        addAllNecessaryAdminAttributes(model, principal);
        log.info("......... current auth: " + SecurityContextHolder.getContext().getAuthentication());
        return "admin";
    }

    @PostMapping
    public String addMovie(@RequestParam("name") String name,
                           @RequestParam("imdb_index") String imdbIndex,
                           @RequestParam("genre") Genre genre,
                           @RequestParam("premiereStart") LocalDate premiereStart,
                           @RequestParam("showEnd") LocalDate showEnd,
                           @RequestParam("hall") String hallStr,
                           @RequestParam("timeSlot") String timeSlot,
                           @RequestParam("timeSlot2") String timeSlot2,
                           @RequestParam("timeSlot3") String timeSlot3,
                           @RequestParam("posterImg") MultipartFile file,
                           Model model, Principal principal) throws IOException {

        Movie movie = new Movie();
        Hall hall = hallRepository.findByName(hallStr).orElseThrow();
        movie.setHall(hall);
        movie.setName(name);
        Schedule schedule = new Schedule();
        schedule.setTimeSlot(timeSlot);
        if (timeSlot2 != null && !"".equals(timeSlot2)) {
            schedule.setTimeSlot2(timeSlot2);
        }
        if (timeSlot3 != null && !"".equals(timeSlot3)) {
            schedule.setTimeSlot3(timeSlot3);
        }
        scheduleRepository.save(schedule);
        movie.setSchedule(schedule);
        movie.setGenre(genre);
        movie.setPremiereStart(premiereStart);
        movie.setShowEnd(showEnd);
        movie.setImdbIndex(imdbIndex);
        uploadFile(file);
        movie.setImgUrl(FileUtils.buildRelativeUrl(
                Paths.get(basePathConfig), Path.of(uploadConfig))
                + "/" + file.getOriginalFilename());
        movieRepository.save(movie);

        addAllNecessaryAdminAttributes(model, principal);
        return "admin";
    }

    private void addAllNecessaryAdminAttributes(Model model, Principal principal) {
        var userEmail = principal.getName();
        var user = userRepository.findByEmail(userEmail).orElseThrow();
        var genreArray = Arrays.stream(Genre.values()).toArray();
        model.addAllAttributes(Map.of(
                "ticketList", ticketRepository.findAll(),
                "movieList", movieRepository.findAll(),
                "userList", userRepository.findAll(),
                "hallList", hallRepository.findAll(),
                "email", userEmail,
                "userStatus", user.getAccessLevel().str,
                "genreArray", genreArray,
                "today", LocalDate.now(),
                "current_time", LocalDateTime.now()));
        model.addAllAttributes(Map.of("hallsNamesList", hallService.getHallsNames()));
    }

    private void uploadFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        assert filename != null;
        Path targetPath = Path.of(uploadConfig).resolve(filename).normalize();
        Files.copy(file.getInputStream(), targetPath,
                StandardCopyOption.REPLACE_EXISTING);
    }
}
