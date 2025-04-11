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
@RequestMapping("/tickets")
public class TicketController {

    public static final String REDIRECT_BUY = "redirect:/buy/";

    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public TicketController(TicketRepository ticketRepository, HallRepository hallRepository, UserRepository userRepository, MovieRepository movieRepository) {
        this.ticketRepository = ticketRepository;
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public String getAll (Model model){
        var ticketList = ticketRepository.findAll();
        model.addAttribute("ticketList", ticketList);
        return "tickets";
    }

    @PostMapping("/addTicket/{hall_id}/{row}/{seat}")
    public String addTicket (@PathVariable ("hall_id") Integer id,
                             @PathVariable ("row") Integer row,
                             @PathVariable ("seat") Integer seat,
                             Model model){

        Ticket ticket = new Ticket();
        Hall hall = hallRepository.findById(id).orElseThrow();
        ticket.setHall(hall);
        User user = userRepository.findById(1L).orElseThrow(); //todo implem current user
        ticket.setUser(user);
        ticket.setTicketStatus(TicketStatus.PAID);
        Movie movie = movieRepository.findById(6L).orElseThrow(); //todo implem curr movie
        ticket.setMovie(movie);
        Schedule schedule = movie.getSchedule();
        ticket.setScheduledOn(LocalDate.now());//todo implement calendar
        ticket.setPurchasedOn(LocalDate.now());
        ticket.setValue(hall.getSeatPrice());
        ticket.setRoww(row);
        ticket.setSeat(seat);
        ticketRepository.save(ticket);
        System.out.println("...created ticket at "
                + ticket.getMovie().getName()
                + " в "+ hall.getName() + " для " +
                user.getName() + " на "+
                schedule.getTimeSlot());
        model.addAllAttributes(Map.of(
                "showIssuedTicket", true,
                "ticket", ticket,
                "schedule", schedule.getTimeSlot(),
                "rows", ticket.getRoww(),
                "seats", ticket.getSeat()));

        return "buy";
    }

}
