package ua.hudyma.Theater2025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.model.*;
import ua.hudyma.Theater2025.repository.HallRepository;
import ua.hudyma.Theater2025.repository.MovieRepository;
import ua.hudyma.Theater2025.repository.TicketRepository;
import ua.hudyma.Theater2025.repository.UserRepository;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    public static final String REDIRECT_BUY = "redirect:/buy/";

    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public AdminController(TicketRepository ticketRepository, HallRepository hallRepository, UserRepository userRepository, MovieRepository movieRepository) {
        this.ticketRepository = ticketRepository;
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public String getEverything(Model model) {
        var ticketList = ticketRepository.findAll();
        var movieList = movieRepository.findAll();
        var userList = userRepository.findAll();
        var hallList = hallRepository.findAll();
        model.addAllAttributes(Map.of(
                "ticketList", ticketList,
                "movieList", movieList, //todo add hall_id to movie
                "userList", userList,
                "hallList", hallList));
        return "admin";
    }



}
