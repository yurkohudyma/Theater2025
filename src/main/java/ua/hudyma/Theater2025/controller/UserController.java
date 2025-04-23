package ua.hudyma.Theater2025.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.model.*;
import ua.hudyma.Theater2025.payment.LiqPayHelper;
import ua.hudyma.Theater2025.repository.*;
import ua.hudyma.Theater2025.service.TicketService;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    public static final String MOVIES_LIST = "moviesList";
    public static final String EMAIL = "email";
    public static final String USER_STATUS = "userStatus";
    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TicketService ticketService;
    private final SeatRepository seatRepository;

    @Value("${liqpay_public_key}")
    private String publicKey;
    @Value("${liqpay_private_key}")
    private String privateKey;

    @GetMapping
    public String getAllMovies(Model model, Principal principal) throws NoSuchAlgorithmException {
        var moviesList = movieRepository.findAll();
        var userEmail = principal.getName();
        var user = userRepository.findByEmail(userEmail).orElseThrow();
        var paymentJSON = LiqPayHelper.preparePayment("10", "UAH", publicKey);
        var paymentData = LiqPayHelper.getData(paymentJSON);
        var paymentSignature = LiqPayHelper.getSignature(paymentData, privateKey);
        model.addAllAttributes(Map.of(
                MOVIES_LIST, moviesList,
                EMAIL, userEmail,
                USER_STATUS, user.getAccessLevel().str,
                "paymentData", paymentData,
                "paymentSignature", paymentSignature));
        return "user";
    }

    @GetMapping("/buy/{hallId}/{movieId}/{selected_timeslot}")
    public String generateTable(Model model, Principal principal,
                                @PathVariable("hallId") Integer hallId,
                                @PathVariable("movieId") Long movieId,
                                @PathVariable("selected_timeslot") String selectedTimeslot) throws NoSuchAlgorithmException {
        var hall = hallRepository.findById(hallId).orElseThrow();

        //List<Seat> soldSeats = seatRepository.findByHallIdAndIsOccupiedTrue(hallId);

        //List<Map<String, Integer>> soldSeatList = getMaps(soldSeats);
        LocalDateTime timeSlotToLocalDateTime =
                ticketService.convertTimeSlotToLocalDateTime(selectedTimeslot);
        List<Ticket> soldTickets = ticketRepository
                .findByHallIdAndMovieIdAndScheduledOn(
                        Long.valueOf(hallId),
                        movieId,
                        timeSlotToLocalDateTime);

        var soldTicketList = getTicketMap(soldTickets);

        //передати напряму сет через thymeleaf не вийде, бо останній серіалізується у звичайний масив
        var moviesList = movieRepository.findAll();
        var userEmail = principal.getName();
        var user = userRepository.findByEmail(userEmail).orElseThrow();
        /*var paymentJSON = LiqPayHelper.preparePayment("10", "UAH");
        var paymentData = LiqPayHelper.getData(paymentJSON);
        var paymentSignature = LiqPayHelper.getSignature(paymentData);*/
        model.addAllAttributes(Map.of(
                "rows", hall.getRowz(),
                "seats", hall.getSeats(),
                "hall", hallId,
                "soldSeatMapList", soldTicketList,
                "movieId", movieId,
                MOVIES_LIST, moviesList,
                EMAIL, userEmail,
                USER_STATUS, user.getAccessLevel().str,
                "selected_timeslot", selectedTimeslot));
        /*model.addAttribute("paymentData", paymentData);
        model.addAttribute("paymentSignature", paymentSignature);*/
        return "user";
    }

    @PostMapping("/buy/{hallId}/{movieId}/{selected_timeslot}/{row}/{seat}")
    public String addTicket(@PathVariable("hallId") Integer hallId,
                            @PathVariable("movieId") Long movieId,
                            @PathVariable("selected_timeslot") String selectedTimeslot,
                            @PathVariable("row") Integer row,
                            @PathVariable("seat") Integer seat,
                            Model model, Principal principal) {

        Ticket ticket = new Ticket();
        Hall hall = hallRepository.findById(hallId).orElseThrow();
        Seat soldSeat = new Seat();
        soldSeat.setOccupied(true);
        soldSeat.setSeatNumber(seat);
        soldSeat.setRowNumber(row);
        soldSeat.setHall(hall);
        ticket.setHall(hall);
        seatRepository.save(soldSeat);

        var email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        LocalDateTime timeSlotToLocalDateTime =
                ticketService.convertTimeSlotToLocalDateTime(selectedTimeslot);
        ticket.setUser(user);
        ticket.setTicketStatus(TicketStatus.RESERVED);
        ticket.setMovie(movie);

        ticket.setScheduledOn(timeSlotToLocalDateTime);
        ticket.setPurchasedOn(LocalDateTime.now());
        ticket.setValue(hall.getSeatPrice());
        ticket.setRoww(row);
        ticket.setSeat(seat);
        ticketRepository.save(ticket);

        log.info("...created ticket at "
                + ticket.getMovie().getName()
                + " в " + hall.getName() + " для " +
                user.getName() + " на " +
                selectedTimeslot);

        /*List<Seat> soldSeats = seatRepository
                .findByHallIdAndIsOccupiedTrue(Math.toIntExact(hallId));*/

        List<Ticket> soldTickets = ticketRepository
                .findByHallIdAndMovieIdAndScheduledOn(
                        Long.valueOf(hallId),
                                 movieId,
                        timeSlotToLocalDateTime);

        var soldTicketList = getTicketMap(soldTickets);
        //List<Map<String, Integer>> soldSeatList = getMaps(soldSeats);
        var moviesList = movieRepository.findAll();

        model.addAllAttributes(Map.of(
                "showIssuedTicket", true,
                "ticket", ticket,
                "selected_timeslot", selectedTimeslot,
                "rows", hall.getRowz(),
                "seats", hall.getSeats(),
                "soldSeatMapList", soldTicketList,
                "movie", movie,
                MOVIES_LIST, moviesList,
                EMAIL, email,
                USER_STATUS, user.getAccessLevel().str));
        return "user";
    }

    private static List<Map<String, Integer>> getMaps(List<Seat> soldSeats) {
        return soldSeats.stream()
                .map(s -> Map.of(
                        "row", s.getRowNumber(),
                        "seat", s.getSeatNumber()))
                .toList();
    }

    private static List<Map<String, Integer>> getTicketMap(List<Ticket> ticketList) {
        return ticketList.stream()
                .map(t -> Map.of(
                        "row", t.getRoww(),
                        "seat", t.getSeat()))
                .toList();
    }
}
