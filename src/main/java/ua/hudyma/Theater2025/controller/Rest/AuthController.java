package ua.hudyma.Theater2025.controller.Rest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {

    @PostMapping("/revoke")
    public ResponseEntity<?> revokeToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Важливо — одразу видаляє
        response.addCookie(cookie);

        log.info("JWT cookie revoked.");

        return ResponseEntity.ok().body("Token revoked successfully.");
    }
}
