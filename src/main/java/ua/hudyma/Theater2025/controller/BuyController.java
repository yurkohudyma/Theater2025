package ua.hudyma.Theater2025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/buy")
public class BuyController {

    @GetMapping
    public String init (){
        return "buy";
    }
}
