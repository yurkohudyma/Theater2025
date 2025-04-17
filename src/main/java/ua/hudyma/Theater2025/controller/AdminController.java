package ua.hudyma.Theater2025.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.repository.HallRepository;
import ua.hudyma.Theater2025.repository.MovieRepository;
import ua.hudyma.Theater2025.repository.TicketRepository;
import ua.hudyma.Theater2025.repository.UserRepository;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/admin")
//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {
    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @GetMapping
    public String getEverything(Model model, Principal principal) {
        var ticketList = ticketRepository.findAll();
        var movieList = movieRepository.findAll();
        var userList = userRepository.findAll();
        var hallList = hallRepository.findAll();
        var userEmail = principal.getName();
        var user = userRepository.findByEmail(userEmail).orElseThrow();
        model.addAllAttributes(Map.of(
                "ticketList", ticketList,
                "movieList", movieList,
                "userList", userList,
                "hallList", hallList,
                "email", userEmail,
                "userStatus", user.getAccessLevel().str));
        System.out.println("......... current auth: " + SecurityContextHolder.getContext().getAuthentication());
        return "admin";
    }



}
