package ua.hudyma.Theater2025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.repository.TicketRepository;

@Controller
@RequestMapping("/tickets")
public class TicketController {
    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    private final TicketRepository ticketRepository;

    @GetMapping
    public String getAll (Model model){
        var ticketList = ticketRepository.findAll();
        model.addAttribute("ticketList", ticketList);
        return "tickets";
    }
}
