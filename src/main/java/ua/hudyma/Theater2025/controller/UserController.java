package ua.hudyma.Theater2025.controller;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.model.*;
import ua.hudyma.Theater2025.repository.*;
import ua.hudyma.Theater2025.service.TicketService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TicketService ticketService;
    private final SeatRepository seatRepository;

    @GetMapping
    public String getAllMovies (Model model, Principal principal){
        var moviesList = movieRepository.findAll();
        var userEmail = principal.getName();
        var user = userRepository.findByEmail(userEmail).orElseThrow();
        model.addAllAttributes(Map.of(
                "moviesList", moviesList,
                "email", userEmail,
                "userStatus", user.getAccessLevel().str));
        return "user";
    }

    @GetMapping("/buy/{hall_id}/{movie_id}")
    public String generateTable(Model model, Principal principal,
                                @PathVariable("hall_id") Integer hall_id,
                                @PathVariable("movie_id") Long movie_id) {
        var hall = hallRepository.findById(hall_id).orElseThrow();

        List<Seat> soldSeats = seatRepository.findByHallIdAndIsOccupiedTrue(hall_id);

        List<Map<String, Integer>> soldSeatList = getMaps(soldSeats);
        //передати напряму сет через thymeleaf не вийде, бо останній серіалізується у звичайний масив
        var moviesList = movieRepository.findAll();
        var userEmail = principal.getName();
        var user = userRepository.findByEmail(userEmail).orElseThrow();
        model.addAllAttributes(Map.of(
                "rows", hall.getRowz(),
                "seats", hall.getSeats(),
                "hall", hall_id,
                "soldSeatMapList", soldSeatList,
                "movie_id", movie_id,
                "moviesList", moviesList,
                "email", userEmail,
                "userStatus", user.getAccessLevel().str));
        return "user";
    }

    @PostMapping("/buy/{hall_id}/{movie_id}/{row}/{seat}")
    public String addTicket(@PathVariable("hall_id") Integer hall_id,
                            @PathVariable("movie_id") Long movie_id,
                            @PathVariable("row") Integer row,
                            @PathVariable("seat") Integer seat,
                            Model model, Principal principal) {

        Ticket ticket = new Ticket();
        Hall hall = hallRepository.findById(hall_id).orElseThrow();
        Seat soldSeat = new Seat();
        soldSeat.setOccupied(true);
        soldSeat.setSeatNumber(seat);
        soldSeat.setRowNumber(row);
        soldSeat.setHall(hall);
        ticket.setHall(hall);
        seatRepository.save(soldSeat);
        var email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Movie movie = movieRepository.findById(movie_id).orElseThrow();
        Schedule schedule = movie.getSchedule();
        LocalDateTime scheduleConvertedToDateTime = ticketService.convertTimeSlotToLocalDateTime(schedule.getTimeSlot());

        ticket.setUser(user);
        ticket.setTicketStatus(TicketStatus.RESERVED);

        ticket.setMovie(movie);

        ticket.setScheduledOn(scheduleConvertedToDateTime);
        ticket.setPurchasedOn(LocalDateTime.now());
        ticket.setValue(hall.getSeatPrice());
        ticket.setRoww(row);
        ticket.setSeat(seat);
        ticketRepository.save(ticket);
        log.info("...created ticket at "
                + ticket.getMovie().getName()
                + " в " + hall.getName() + " для " +
                user.getName() + " на " +
                schedule.getTimeSlot());

        List<Seat> soldSeats = seatRepository.findByHallIdAndIsOccupiedTrue(Math.toIntExact(hall_id));

        List<Map<String, Integer>> soldSeatList = getMaps(soldSeats);
        var moviesList = movieRepository.findAll();

        model.addAllAttributes(Map.of(
                "showIssuedTicket", true,
                "ticket", ticket,
                "schedule", schedule.getTimeSlot(),
                "rows", hall.getRowz(),
                "seats", hall.getSeats(),
                "soldSeatMapList", soldSeatList,
                "movie", movie,
                "moviesList", moviesList,
                "email", email,
                "userStatus", user.getAccessLevel().str));
        return "user";
    }

    private static List<Map<String, Integer>> getMaps(List<Seat> soldSeats) {
        return soldSeats.stream()
                .map(s -> Map.of(
                        "row", s.getRowNumber(),
                        "seat", s.getSeatNumber()))
                .toList();
    }
}
