package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestSecurityController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Це відкритий endpoint";
    }

    @GetMapping("/secure")
    public String secureEndpoint() {
        return "Це захищений endpoint";
    }
}
