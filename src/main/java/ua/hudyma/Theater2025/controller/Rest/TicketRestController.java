package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.model.Hall;
import ua.hudyma.Theater2025.model.Movie;
import ua.hudyma.Theater2025.model.Ticket;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.*;
import ua.hudyma.Theater2025.service.TicketService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

@RestController
@RequestMapping("/api/tickets")
public class TicketRestController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final HallRepository hallRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;
    private final TicketService ticketService;

    public TicketRestController(TicketRepository ticketRepository,
                                UserRepository userRepository,
                                HallRepository hallRepository,
                                MovieRepository movieRepository, SeatRepository seatRepository, TicketService ticketService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.hallRepository = hallRepository;
        this.movieRepository = movieRepository;
        this.seatRepository = seatRepository;
        this.ticketService = ticketService;
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<Ticket>> getAll() {
        System.out.println("......... current auth: " + SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ticketRepository.findAll());
    }

    @GetMapping("{id}")
    public Ticket getById(@PathVariable("id") Long id) {
        return ticketRepository.findById(id).orElseThrow();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTicket(@RequestBody Ticket ticket) {
        ticket.setPurchasedOn(LocalDate.now());

        ticket.setTicketStatus(TicketStatus.PAID);
        User user = userRepository.findById(ticket.getUser().getId()).orElseThrow();
        ticket.setUser(user);
        Movie movie = movieRepository.findById(ticket.getMovie().getId()).orElseThrow();
        ticket.setMovie(movie);
        Hall hall = hallRepository.findById(ticket.getHall().getId()).orElseThrow();
        LocalDateTime scheduleConvertedToDateTime = ticketService.convertTimeSlotToLocalDateTime(movie.getSchedule().getTimeSlot());
        ticket.setScheduledOn(scheduleConvertedToDateTime);

        ticket.setHall(hall);
        ticketRepository.save(ticket);
        out.println("......added ticket on " + ticket.getMovie().getName() + " for " + ticket.getUser().getName());
    }

    @PostMapping("/addAll")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAll(@RequestBody Ticket[] tickets) {
        Arrays.stream(tickets).forEach(this::addTicket);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        ticketRepository.deleteAllTickets();
        out.println("....deleting all tickets");
        seatRepository.deleteAllSeats();
        out.println("....deleting all Seats");
    }
}
