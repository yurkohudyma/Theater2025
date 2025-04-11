package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.repository.HallRepository;

@Controller
@RequestMapping("/buy")
public class BuyController {

    private final HallRepository hallRepository;

    public BuyController(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @GetMapping("/{id}")
    public String generateTable (Model model, @PathVariable ("id") Long id){
        var hall = hallRepository.findById(id).orElseThrow();
        model.addAttribute("rows", hall.getRowz());
        model.addAttribute("seats", hall.getSeats());
        model.addAttribute("hall", id);
        return "buy";

    }
}
