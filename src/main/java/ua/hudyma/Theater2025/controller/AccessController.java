package ua.hudyma.Theater2025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccessController {

    @RequestMapping("/checkout")
    public String checkout(@CookieValue(name = "order", required = false) String orderCookie) {
        if (orderCookie == null) {
            System.out.println("Cookie 'order' is missing!");
        } else {
            System.out.println("Cookie value: " + orderCookie);
        }
        return "checkout";
    }


}
