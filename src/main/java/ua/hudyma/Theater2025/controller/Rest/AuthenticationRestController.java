package ua.hudyma.Theater2025.controller.Rest;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.Theater2025.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    //private final JwtService jwtService;

    /*public AuthenticationRestController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }*/

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            // Спочатку намагаємось аутентифікувати користувача
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.email, authRequest.password)
            );

            // Якщо аутентифікація успішна, генеруємо токен
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(String.valueOf(authentication));

            // Відправляємо токен в HTTP cookie
            var cookie = new Cookie("Authorization", token);
            cookie.setHttpOnly(true);  // Безпечний доступ
            cookie.setSecure(true);    // Тільки для HTTPS з'єднань
            cookie.setPath("/");       // Доступність для всього домену
            cookie.setMaxAge(60 * 60 * 24); // Термін дії cookie - 1 день

            // Відправляємо cookie у заголовку відповіді
            ResponseEntity.ok().header("Set-Cookie", cookie.toString());

            return ResponseEntity.ok("Успішно авторизовано!");
        } catch (BadCredentialsException e) {
            // Якщо логін або пароль неправильні
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Невірні дані для входу");
        }
    }


















   /* @PostMapping("/loginOld")
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
    }*/


    public record AuthRequest(String email, String password) {}
}
