package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.model.Hall;
import ua.hudyma.Theater2025.model.Movie;
import ua.hudyma.Theater2025.model.Ticket;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.HallRepository;
import ua.hudyma.Theater2025.repository.MovieRepository;
import ua.hudyma.Theater2025.repository.TicketRepository;
import ua.hudyma.Theater2025.repository.UserRepository;

import java.time.LocalDate;
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

    public TicketRestController(TicketRepository ticketRepository,
                                UserRepository userRepository,
                                HallRepository hallRepository,
                                MovieRepository movieRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.hallRepository = hallRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public List<Ticket> getAll (){
        return ticketRepository.findAll();
    }

    @GetMapping("{id}")
    public Ticket getById (@PathVariable ("id") Long id){
        return ticketRepository.findById(id).orElseThrow();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTicket (@RequestBody Ticket ticket){
        ticket.setPurchasedOn(LocalDate.now());
        ticket.setScheduledOn(LocalDate.now().plusDays(1));
        ticket.setTicketStatus(TicketStatus.PAID);
        User user = userRepository.findById(ticket.getUser().getId()).orElseThrow();
        ticket.setUser(user);
        Movie movie = movieRepository.findById(ticket.getMovie().getId()).orElseThrow();
        ticket.setMovie(movie);
        Hall hall = hallRepository.findById(ticket.getHall().getId()).orElseThrow();
        ticket.setHall(hall);
        ticketRepository.save(ticket);
        out.println("......added ticket on "+ ticket.getMovie().getName() + " for " + ticket.getUser().getName());
    }

    @PostMapping("/addAll")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAll (@RequestBody Ticket[] tickets){
        Arrays.stream(tickets).forEach(this::addTicket);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll (){
        out.println("....deleting all tickets");
        ticketRepository.findAll().forEach(ticketRepository::delete);
    }

}
