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
import ua.hudyma.Theater2025.constants.liqpay.OrderStatus;
import ua.hudyma.Theater2025.model.Order;
import ua.hudyma.Theater2025.model.SeatBatchRequest;
import ua.hudyma.Theater2025.model.Ticket;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.HallRepository;
import ua.hudyma.Theater2025.repository.MovieRepository;
import ua.hudyma.Theater2025.repository.TicketRepository;
import ua.hudyma.Theater2025.repository.UserRepository;
import ua.hudyma.Theater2025.service.AuthService;
import ua.hudyma.Theater2025.service.OrderService;
import ua.hudyma.Theater2025.service.TicketService;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static ua.hudyma.Theater2025.payment.LiqPayHelper.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    public static final String MOVIES_LIST = "moviesList", EMAIL = "email", USER_STATUS = "userStatus",
            PAYMENT_DATA = "paymentData", AUTH_IS_NULL = "authIsNull";
    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final OrderService orderService;
    private final TicketService ticketService;
    private final AuthService authService;

    @Value("${liqpay_public_key}")
    private String publicKey;
    @Value("${liqpay_private_key}")
    private String privateKey;
    @Value("${liqpay_server_url}")
    private String serverUrl;

    @GetMapping
    public String getAllMovies(Model model, Principal principal,
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
                    AUTH_IS_NULL, authIsNull));
            log.info("...............user " + principal.getName() + " authNULL is " + authIsNull);
            getTicketsAndSupplyWithQRCodes(model, user);
        } else { //auth is NULL
            model.addAllAttributes(Map.of(
                    MOVIES_LIST, moviesList,
                    AUTH_IS_NULL, true));
        }
        return "user";
    }

    private void getTicketsAndSupplyWithQRCodes(Model model, User user) {
        var ticketList = ticketRepository
                .findByUserIdAndTicketStatus(
                        user.getId(),
                        TicketStatus.PAID);
        var qrCodesList = ticketService.getQRCodesList(ticketList);
        if (!ticketList.isEmpty()) {
            model.addAllAttributes(Map.of(
                    "ticketList", ticketList,
                    "qrCodesList", qrCodesList,
                    "showTickets", true
            ));
        }
    }

    /**
     * ендпойнт для виведення схеми кінозалу
     */

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
        var soldTicketList = TicketService.getTicketMap(soldTickets);

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
                "selected_timeslot", selectedTimeslot,
                "ticketPrice", hall.getSeatPrice()));
        model.addAttribute("userId", user.getId());
        getTicketsAndSupplyWithQRCodes(model, user);
        return "user";
    }

    @PostMapping("/buy/{hallId}/{movieId}/{selected_timeslot}")
    public String generateTableNew(Model model, Principal principal,
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
        var soldTicketList = TicketService.getTicketMap(soldTickets);

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
                "selected_timeslot", selectedTimeslot,
                "ticketPrice", hall.getSeatPrice()));
        model.addAttribute("userId", user.getId());
        getTicketsAndSupplyWithQRCodes(model, user);
        return "user";
    }

    /**
     * ендпойнт для ajax-отримання ряду та місця квитка та формування paymentData
     */
    @PostMapping("/updateRowSeatDataBatch")
    @ResponseBody
    public Map<String, String> getUpdatedPaymentData(
            @RequestBody SeatBatchRequest seatBatchRequest,
            Principal principal) throws NoSuchAlgorithmException {
        var reqUnitList = seatBatchRequest.seats();
        String orderId = UUID.randomUUID().toString();
        var timeSlotToLocalDateTime =
                ticketService.convertTimeSlotToLocalDateTime(seatBatchRequest.timeslot());
        var userEmail = principal.getName();
        String paymentDescription = "Квиток(-ки) на сеанс "
                + timeSlotToLocalDateTime + " " + userEmail;
        var amount = hallRepository
                .findById(seatBatchRequest.hallId())
                .orElseThrow()
                .getSeatPrice();
        amount *= reqUnitList.size();

        var paymentJSON = preparePayment(
                amount.toString(),
                publicKey,
                paymentDescription,
                orderId,
                serverUrl
        );

        var paymentData = getPaymentData(paymentJSON);
        var paymentSignature = getPaymentSignature(paymentData, privateKey);
        var order = Order
                .builder()
                .status(OrderStatus.PENDING)
                .createdOn(LocalDateTime.now())
                .orderId(orderId)
                .requestedSeats(seatBatchRequest)
                .build();

        orderService.storeOrderInMemoryMap(order);

        log.info(":::::::: order has been requested {}", order);
        order
                .requestedSeats()
                .seats()
                .forEach(log::info);

        return Map.of(PAYMENT_DATA, paymentData, "signature", paymentSignature);
    }
}


