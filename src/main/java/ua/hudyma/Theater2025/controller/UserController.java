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
import ua.hudyma.Theater2025.model.*;
import ua.hudyma.Theater2025.repository.HallRepository;
import ua.hudyma.Theater2025.repository.MovieRepository;
import ua.hudyma.Theater2025.repository.TicketRepository;
import ua.hudyma.Theater2025.repository.UserRepository;
import ua.hudyma.Theater2025.service.AuthService;
import ua.hudyma.Theater2025.service.OrderService;
import ua.hudyma.Theater2025.service.ScheduleService;
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
    public static final String MOVIES_SCHEDULE_MAP = "moviesScheduleMap";
    public static final String TICKET_PRICE = "ticketPrice";
    public static final String SELECTED_TIMESLOT = "selected_timeslot";
    public static final String MOVIE_ID = "movieId";
    public static final String SOLD_SEAT_MAP_LIST = "soldSeatMapList";
    public static final String HALL_ID = "hallId";
    public static final String SEATS = "seats";
    public static final String ROWS = "rows";
    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final OrderService orderService;
    private final TicketService ticketService;
    private final AuthService authService;
    private final ScheduleService scheduleService;

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
        var movieSchedulesMap = scheduleService
                .getMovieScheduleMap(moviesList);
        if (authentication != null) {
            var userEmail = principal.getName();
            var user = userRepository.findByEmail(userEmail).orElseThrow();
            var authIsNull = authService.currentAuthIsNullOrAnonymous();
            model.addAllAttributes(Map.of(
                    EMAIL, userEmail,
                    USER_STATUS, user.getAccessLevel().str,
                    AUTH_IS_NULL, authIsNull));
            log.info("...............user " + principal.getName() + " authNULL is " + authIsNull);
            getTicketsAndSupplyWithQRCodes(model, user);
        } else { //auth is NULL
            model.addAttribute(AUTH_IS_NULL, true);
        }
        model.addAttribute(MOVIES_SCHEDULE_MAP, movieSchedulesMap);
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
                                Authentication authentication,
                                @PathVariable(HALL_ID) Integer hallId,
                                @PathVariable(MOVIE_ID) Long movieId,
                                @PathVariable(SELECTED_TIMESLOT) String selectedTimeslot) {

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
        var movieSchedulesMap = scheduleService
                .getMovieScheduleMap(moviesList);

        provideWithModelAttribOnAuthIsTrue(model, principal, authentication, hallId, movieId, selectedTimeslot, hall, soldTicketList, movieSchedulesMap);
        return "user";
    }

    private void provideWithModelAttribOnAuthIsTrue(Model model,
                                                    Principal principal,
                                                    Authentication authentication,
                                                    Integer hallId,
                                                    Long movieId,
                                                    String selectedTimeslot,
                                                    Hall hall,
                                                    List<Map<String, Integer>> soldTicketList,
                                                    Map<Movie, List<String>> movieSchedulesMap) {
        if (authentication != null) {
            var userEmail = principal.getName();
            var user = userRepository.findByEmail(userEmail).orElseThrow();

            model.addAllAttributes(Map.of(
                    ROWS, hall.getRowz(),
                    SEATS, hall.getSeats(),
                    HALL_ID, hallId,
                    SOLD_SEAT_MAP_LIST, soldTicketList,
                    MOVIE_ID, movieId,
                    MOVIES_SCHEDULE_MAP, movieSchedulesMap,
                    EMAIL, userEmail,
                    USER_STATUS, user.getAccessLevel().str,
                    SELECTED_TIMESLOT, selectedTimeslot,
                    TICKET_PRICE, hall.getSeatPrice()));
            model.addAttribute("userId", user.getId());
            getTicketsAndSupplyWithQRCodes(model, user);
        } else {
            model.addAllAttributes(Map.of(
                    ROWS, hall.getRowz(),
                    SEATS, hall.getSeats(),
                    HALL_ID, hallId,
                    SOLD_SEAT_MAP_LIST, soldTicketList,
                    MOVIE_ID, movieId,
                    MOVIES_SCHEDULE_MAP, movieSchedulesMap,
                    SELECTED_TIMESLOT, selectedTimeslot,
                    TICKET_PRICE, hall.getSeatPrice(),
                    AUTH_IS_NULL, true));
        }
    }

    @PostMapping("/buy/{hallId}/{movieId}/{selected_timeslot}")
    public String generateTableNew(Model model, Principal principal,
                                   Authentication authentication,
                                   @PathVariable(HALL_ID) Integer hallId,
                                   @PathVariable(MOVIE_ID) Long movieId,
                                   @PathVariable(SELECTED_TIMESLOT) String selectedTimeslot) {

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
        var movieSchedulesMap = scheduleService
                .getMovieScheduleMap(moviesList);
       /* var userEmail = principal.getName();
        var user = userRepository.findByEmail(userEmail).orElseThrow();

        model.addAllAttributes(Map.of(
                ROWS, hall.getRowz(),
                SEATS, hall.getSeats(),
                HALL_ID, hallId,
                SOLD_SEAT_MAP_LIST, soldTicketList,
                MOVIE_ID, movieId,
                MOVIES_SCHEDULE_MAP, movieSchedulesMap,
                EMAIL, userEmail,
                USER_STATUS, user.getAccessLevel().str,
                SELECTED_TIMESLOT, selectedTimeslot,
                TICKET_PRICE, hall.getSeatPrice()));
        model.addAttribute("userId", user.getId());
        getTicketsAndSupplyWithQRCodes(model, user);*/
        provideWithModelAttribOnAuthIsTrue(model,
                principal, authentication, hallId, movieId,
                selectedTimeslot, hall, soldTicketList, movieSchedulesMap);
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


