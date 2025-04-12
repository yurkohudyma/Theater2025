package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.constants.service.AdminService;
import ua.hudyma.Theater2025.model.*;
import ua.hudyma.Theater2025.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/buy")
public class BuyController {

    private final TicketRepository ticketRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final AdminService adminService;
    private final SeatRepository seatRepository;

    public BuyController(TicketRepository ticketRepository, HallRepository hallRepository, UserRepository userRepository, MovieRepository movieRepository, AdminService adminService, SeatRepository seatRepository) {
        this.ticketRepository = ticketRepository;
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.adminService = adminService;
        this.seatRepository = seatRepository;
    }

    @GetMapping("/{id}")
    public String generateTable(Model model, @PathVariable("id") Long id) {
        var hall = hallRepository.findById(id).orElseThrow();

        List<Seat> soldSeats = seatRepository.findByHallIdAndIsOccupiedTrue(Math.toIntExact(id));

        List<Map<String, Integer>> soldSeatList = soldSeats.stream()
                .map(s -> Map.of("row", s.getRowNumber(), "seat", s.getSeatNumber()))
                .toList();

        model.addAttribute("rows", hall.getRowz());
        model.addAttribute("seats", hall.getSeats());
        model.addAttribute("hall", id);
        model.addAttribute("soldSeatList", soldSeatList);
        return "buy";
    }

    @PostMapping("/{hall_id}/{row}/{seat}")
    public String addTicket(@PathVariable("hall_id") Integer id,
                            @PathVariable("row") Integer row,
                            @PathVariable("seat") Integer seat,
                            Model model) {

        Ticket ticket = new Ticket();
        Hall hall = hallRepository.findById(id).orElseThrow();
        Seat soldSeat = new Seat();
        soldSeat.setOccupied(true);
        soldSeat.setSeatNumber(seat);
        soldSeat.setRowNumber(row);
        soldSeat.setHall(hall);
        ticket.setHall(hall);
        seatRepository.save(soldSeat);
        //hall.addTicket(ticket);

        User user = userRepository.findById(1L).orElseThrow(); //todo implem current user
        ticket.setUser(user);
        ticket.setTicketStatus(TicketStatus.RESERVED);
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
                + " в " + hall.getName() + " для " +
                user.getName() + " на " +
                schedule.getTimeSlot());

        model.addAllAttributes(Map.of(
                "showIssuedTicket", true,
                "ticket", ticket,
                "schedule", schedule.getTimeSlot(),
                "rows", hall.getRowz(),
                "seats", hall.getSeats()));
        return "buy";
    }
}
