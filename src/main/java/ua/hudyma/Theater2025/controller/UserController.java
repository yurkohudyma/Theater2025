package ua.hudyma.Theater2025.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.model.Ticket;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.*;
import ua.hudyma.Theater2025.service.AuthService;
import ua.hudyma.Theater2025.service.TicketService;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Comparator.comparing;
import static ua.hudyma.Theater2025.payment.LiqPayHelper.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    public static final String MOVIES_LIST = "moviesList";
    public static final String EMAIL = "email";
    public static final String USER_STATUS = "userStatus";
    public static final String PAYMENT_DATA = "paymentData";
    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TicketService ticketService;
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
        if (authentication != null) {
            var userEmail = principal.getName();
            var user = userRepository.findByEmail(userEmail).orElseThrow();
            var authIsNull = authService.currentAuthIsNullOrAnonymous();
            model.addAllAttributes(Map.of(
                    MOVIES_LIST, moviesList,
                    EMAIL, userEmail,
                    USER_STATUS, user.getAccessLevel().str,
                    "authIsNull", authIsNull));
            log.info("...............user " + principal.getName() + " authNULL is " + authIsNull);
            var ticket = getTicket(user);
            if (ticket.isPresent()) {
                model.addAttribute("ticket", ticket.orElseThrow());
                model.addAttribute("showIssuedTicket", true);
            }
        } else { //auth is NULL
            model.addAllAttributes(Map.of(
                    MOVIES_LIST, moviesList,
                    "authIsNull", true));
        }
        return "user";
    }

    private Optional<Ticket> getTicket(User user) {
        var ticketList = ticketRepository
                .findByUserIdAndTicketStatus(user.getId(), TicketStatus.PAID);
        return ticketList.size() > 1 ? ticketList
                .stream()
                .max(comparing(Ticket::getScheduledOn))
                : Optional.ofNullable(ticketList.get(0));
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
        var timeSlotToLocalDateTime =
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

        model.addAllAttributes(Map.of(
                "rows", hall.getRowz(),
                "seats", hall.getSeats(),
                "hallId", hallId,
                "soldSeatMapList", soldTicketList,
                "movieId", movieId,
                MOVIES_LIST, moviesList,
                EMAIL, userEmail,
                USER_STATUS, user.getAccessLevel().str,
                "selected_timeslot", selectedTimeslot));
        var ticket = getTicket(user);
        if (ticket.isPresent()) {
            model.addAttribute("ticket", ticket.orElseThrow());
            model.addAttribute("showIssuedTicket", true);
        }
        return "user";
    }

    /**
     * ендпойнт для ajax-отримання ряду та місця квитка та формування paymentData
     */
    @PostMapping("/updateRowSeatData")
    @ResponseBody
    public Map<String, String> getUpdatedPaymentData(
            @RequestBody SeatRequest req,
            Principal principal) throws NoSuchAlgorithmException {

        String orderId = UUID.randomUUID() + "_r" + req.row() + "_s" + req.seat();
        var timeSlotToLocalDateTime =
                ticketService.convertTimeSlotToLocalDateTime(req.timeslot());
        var userEmail = principal.getName();
        String paymentDescription = "Квиток на сеанс " + timeSlotToLocalDateTime + " " + userEmail;
        var amount = hallRepository
                .findById(req.hallId())
                .orElseThrow()
                .getSeatPrice()
                .toString();

        var paymentJSON = preparePayment(
                amount,
                publicKey,
                paymentDescription,
                orderId,
                serverUrl
        );

        var paymentData = getPaymentData(paymentJSON);
        var paymentSignature = getPaymentSignature(paymentData, privateKey);

        var user = userRepository.findByEmail(principal.getName()).orElseThrow();
        var hall = hallRepository.findById(req.hallId()).orElseThrow();
        var movie = movieRepository.findById(req.movieId()).orElseThrow();

        var draftTicket = Ticket.builder()
                .ticketStatus(TicketStatus.PENDING)
                .hall(hall)
                .movie(movie)
                .user(user)
                .orderId(orderId)
                .scheduledOn(timeSlotToLocalDateTime)
                .build();

        ticketRepository.save(draftTicket);
        return Map.of(PAYMENT_DATA, paymentData, "signature", paymentSignature);
    }

    private static List<Map<String, Integer>> getTicketMap(List<Ticket> ticketList) {
        return ticketList.stream()
                .map(t -> Map.of(
                        "row", t.getRoww(),
                        "seat", t.getSeat()))
                .toList();
    }
}

record SeatRequest(int row, int seat, String timeslot, Long movieId, Long hallId) {}