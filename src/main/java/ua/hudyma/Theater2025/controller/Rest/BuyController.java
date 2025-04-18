package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.service.AdminService;
import ua.hudyma.Theater2025.model.*;
import ua.hudyma.Theater2025.repository.*;
import ua.hudyma.Theater2025.service.TicketService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/buy")
public class BuyController {

    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TicketService ticketService;
    private final SeatRepository seatRepository;

    public BuyController(TicketRepository ticketRepository, HallRepository hallRepository, UserRepository userRepository, MovieRepository movieRepository, AdminService adminService, TicketService ticketService, SeatRepository seatRepository) {
        this.ticketRepository = ticketRepository;
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.ticketService = ticketService;
        this.seatRepository = seatRepository;
    }

    @GetMapping("/{hall_id}/{movie_id}")
    public String generateTable(Model model,
                                @PathVariable("hall_id") Integer hall_id,
                                @PathVariable("movie_id") Long movie_id) {
        var hall = hallRepository.findById(hall_id).orElseThrow();

        List<Seat> soldSeats = seatRepository.findByHallIdAndIsOccupiedTrue(hall_id);

        List<Map<String, Integer>> soldSeatList = getMaps(soldSeats);
        //передати напряму сет через thymeleaf не вийде, бо останній серіалізується у звичайний масив

        model.addAllAttributes(Map.of(
                "rows", hall.getRowz(),
                "seats", hall.getSeats(),
                "hall", hall_id,
                "soldSeatMapList", soldSeatList,
                "movie_id", movie_id));
        return "buy";
    }

    @PostMapping("/{hall_id}/{movie_id}/{row}/{seat}")
    public String addTicket(@PathVariable("hall_id") Integer hall_id,
                            @PathVariable("movie_id") Long movie_id,
                            @PathVariable("row") Integer row,
                            @PathVariable("seat") Integer seat,
                            Model model) {

        Ticket ticket = new Ticket();
        Hall hall = hallRepository.findById(hall_id).orElseThrow();
        Seat soldSeat = new Seat();
        soldSeat.setOccupied(true);
        soldSeat.setSeatNumber(seat);
        soldSeat.setRowNumber(row);
        soldSeat.setHall(hall);
        ticket.setHall(hall);
        seatRepository.save(soldSeat);

        int userListSize = userRepository.findAll().size();
        long userRandomize = new Random().nextInt(userListSize);
        userRandomize = userRandomize == 0 ? userRandomize + 1 : userRandomize;
        User user = userRepository.findById(userRandomize).orElseThrow(); //todo implem current user
        ticket.setUser(user);
        ticket.setTicketStatus(TicketStatus.RESERVED);
        Movie movie = movieRepository.findById(movie_id).orElseThrow();
        ticket.setMovie(movie);
        Schedule schedule = movie.getSchedule();
        LocalDateTime scheduleConvertedToDateTime = ticketService.convertTimeSlotToLocalDateTime(schedule.getTimeSlot());
        ticket.setScheduledOn(scheduleConvertedToDateTime);
        ticket.setPurchasedOn(LocalDate.now());
        ticket.setValue(hall.getSeatPrice());
        ticket.setRoww(row);
        ticket.setSeat(seat);
        ticketRepository.save(ticket);
        System.out.println("...created ticket at "
                + ticket.getMovie().getName()
                + " в " + hall.getName() + " для " +
                user.getName() + " на " +
                schedule.getTimeSlot());

        List<Seat> soldSeats = seatRepository.findByHallIdAndIsOccupiedTrue(Math.toIntExact(hall_id));

        List<Map<String, Integer>> soldSeatList = getMaps(soldSeats);

        model.addAllAttributes(Map.of(
                "showIssuedTicket", true,
                "ticket", ticket,
                "schedule", schedule.getTimeSlot(),
                "rows", hall.getRowz(),
                "seats", hall.getSeats(),
                "soldSeatMapList", soldSeatList,
                "movie", movie));
        return "buy";
    }

    private static List<Map<String, Integer>> getMaps(List<Seat> soldSeats) {
        return soldSeats.stream()
                .map(s -> Map.of(
                        "row", s.getRowNumber(),
                        "seat", s.getSeatNumber()))
                .toList();
    }
}
