package ua.hudyma.Theater2025.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.model.*;
import ua.hudyma.Theater2025.repository.*;
import ua.hudyma.Theater2025.service.AuthService;
import ua.hudyma.Theater2025.service.TicketService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ua.hudyma.Theater2025.payment.LiqPayHelper.*;

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
    private final AuthService authService;

    @Value("${liqpay_public_key}")
    private String publicKey;
    @Value("${liqpay_private_key}")
    private String privateKey;
    @Value("${liqpay_server_url}")
    private String serverUrl;

    @GetMapping
    public String getAllMovies(Model model,
                               Principal principal,
                               Authentication authentication) {
        var moviesList = movieRepository.findAll();
        if (authentication != null){
            var userEmail = principal.getName();
            var user = userRepository.findByEmail(userEmail).orElseThrow();
            var authIsNull = authService.currentAuthIsNullOrAnonymous();
            model.addAllAttributes(Map.of(
                    MOVIES_LIST, moviesList,
                    EMAIL, userEmail,
                    USER_STATUS, user.getAccessLevel().str,
                    "authIsNull", authIsNull));
            log.info("...............user " + principal.getName() + " authNULL is " + authIsNull);
        }

        else {
            model.addAllAttributes(Map.of(
                    MOVIES_LIST, moviesList,
                    "authIsNull", true));
        }
        return "user";
    }



    /**
     * ендпойнт для виведення схеми кінозалу
     */
    @SneakyThrows
    @GetMapping("/buy/{hallId}/{movieId}/{selected_timeslot}")
    public String generateTable(Model model, Principal principal,
                                @PathVariable("hallId") Integer hallId,
                                @PathVariable("movieId") Long movieId,
                                @PathVariable("selected_timeslot") String selectedTimeslot) {
        var hall = hallRepository.findById(hallId).orElseThrow();
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

        String paymentDescription = "Квиток на сеанс " + selectedTimeslot + " " + LocalDate.now() + " " + userEmail;
        var paymentJSON = preparePayment(hall.getSeatPrice().toString(),
                "UAH",
                publicKey,
                paymentDescription, serverUrl);
        var paymentData = getData(paymentJSON);
        var paymentSignature = getSignature(paymentData, privateKey);

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
        model.addAttribute("paymentData", paymentData);
        model.addAttribute("paymentSignature", paymentSignature);
        return "user";
    }

    /**
     * придбання квитка і оновлення схеми кінозалу
     */
    @SneakyThrows
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
        List<Ticket> soldTickets = ticketRepository
                .findByHallIdAndMovieIdAndScheduledOn(
                        Long.valueOf(hallId),
                                 movieId,
                        timeSlotToLocalDateTime);

        var soldTicketList = getTicketMap(soldTickets);
        var moviesList = movieRepository.findAll();
        String paymentDescription = "Квиток на сеанс " + selectedTimeslot;
        var paymentJSON = preparePayment(hall.getSeatPrice().toString(),
                "UAH",
                publicKey,
                paymentDescription,
                serverUrl);
        var paymentData = getData(paymentJSON);
        var paymentSignature = getSignature(paymentData, privateKey);

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
        model.addAttribute("paymentData", paymentData);
        model.addAttribute("paymentSignature", paymentSignature);
        return "user";
    }

    private static List<Map<String, Integer>> getTicketMap(List<Ticket> ticketList) {
        return ticketList.stream()
                .map(t -> Map.of(
                        "row", t.getRoww(),
                        "seat", t.getSeat()))
                .toList();
    }
}
