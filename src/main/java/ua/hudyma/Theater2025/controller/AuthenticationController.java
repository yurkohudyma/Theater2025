package ua.hudyma.Theater2025.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.Theater2025.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String authenticate(@RequestBody AuthRequest request) {
        try {
            System.out.println("START AUTHENTICATION");

            // Спочатку аутентифікація
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            System.out.println("LOGIN OK");

            if (authentication.isAuthenticated()) {
                // Генеруємо токен
                return jwtService.generateToken(request.email());
            } else {
                throw new RuntimeException("Invalid credentials");
            }
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Authentication failed", e);
        }
    }


    public record AuthRequest(String email, String password) {}
}
