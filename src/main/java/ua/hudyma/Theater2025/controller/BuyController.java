package ua.hudyma.Theater2025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/buy")
public class BuyController {

    @PostMapping
    public String engageHallScheme (
            @RequestParam Long hallId,
            @RequestParam Long movieId,
            @RequestParam String timeSlot
    ) {
        return "forward:/user/buy/" + hallId + "/" + movieId + "/" + timeSlot;
    }
}
