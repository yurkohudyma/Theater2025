package ua.hudyma.Theater2025.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.liqpay.OrderStatus;
import ua.hudyma.Theater2025.model.Order;
import ua.hudyma.Theater2025.model.SeatBatchRequest;
import ua.hudyma.Theater2025.model.Ticket;
import ua.hudyma.Theater2025.repository.HallRepository;
import ua.hudyma.Theater2025.repository.MovieRepository;
import ua.hudyma.Theater2025.repository.TicketRepository;
import ua.hudyma.Theater2025.repository.UserRepository;
import ua.hudyma.Theater2025.service.AuthService;
import ua.hudyma.Theater2025.service.OrderService;
import ua.hudyma.Theater2025.service.TicketService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public static final String AUTH_IS_NULL = "authIsNull";
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
                    AUTH_IS_NULL, authIsNull));
            log.info("...............user " + principal.getName() + " authNULL is " + authIsNull);
            var ticket = ticketService.getLastIssuedTicket(user);
            if (ticket.isPresent()) {
                model.addAllAttributes(Map.of(
                        "ticket", ticket.orElseThrow(),
                        "showIssuedTicket", true,
                        "id", ticket.orElseThrow().getId()));
            }
        } else { //auth is NULL
            model.addAllAttributes(Map.of(
                    MOVIES_LIST, moviesList,
                    AUTH_IS_NULL, true));
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
        var ticket = ticketService.getLastIssuedTicket(user);
        if (ticket.isPresent()) {
            model.addAttribute("ticket", ticket.orElseThrow());
            model.addAttribute("showIssuedTicket", true);
            model.addAttribute("id", ticket.orElseThrow().getId());
        }
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

        //тепер записувати у номер ордера перший квиток замовлення
        var reqUnitList = seatBatchRequest.seats();
        //var initTicket = reqUnitList.get(0);

        String orderId = UUID.randomUUID().toString()/* + "_r" + initTicket.row() + "_s" + initTicket.seat()*/;
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

        /*var user = userRepository.findByEmail(principal.getName()).orElseThrow();
        var hall = hallRepository.findById(seatBatchRequest.hallId()).orElseThrow();
        var movie = movieRepository.findById(seatBatchRequest.movieId()).orElseThrow();*/

       /* @Deprecated
        var draftTicket = Ticket.builder()
                .ticketStatus(TicketStatus.PENDING)
                .hall(hall)
                .movie(movie)
                .user(user)
                .orderId(orderId)
                .scheduledOn(timeSlotToLocalDateTime)
                .build()*/;

        var order = Order
                .builder()
                .status(OrderStatus.PENDING)
                .createdOn(LocalDateTime.now())
                .orderId(orderId)
                .requestedSeats(seatBatchRequest)
                .build();

        orderService.storeOrderInMemoryMap(order);

        //orderService.serializeOrder(order);

        //orderService.saveOrderToCookie(response, order);

        //ticketRepository.save(draftTicket);
        //orderRepository.save(order);
        log.info(":::::::: order has been requested {}", order);
        order
                .requestedSeats()
                .seats()
                .forEach(log::info);

        return Map.of(PAYMENT_DATA, paymentData, "signature", paymentSignature);
    }
}
