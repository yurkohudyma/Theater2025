package ua.hudyma.Theater2025.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.Theater2025.security.JwtTokenProvider;
import ua.hudyma.Theater2025.service.AuthService;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/checkOld")
    public ResponseEntity<?> authCheck(Principal principal) {
        var authIsNull = authService.currentAuthIsNullOrAnonymous();
        if (principal != null){
            log.info("..... " + principal.getName() + " auth is NULL " +  authIsNull);
        }
        else {
            log.info("..... auth is NULL " +  authIsNull);
        }
        return authIsNull ?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                : ResponseEntity.ok().build();

    }

    @GetMapping("/check")
    public ResponseEntity<?> authCheck() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authService.getTokenFromCookie(request, "RefreshToken");

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            String newAccessToken = jwtTokenProvider.generateToken(email);

            Cookie accessCookie = new Cookie("Authorization", newAccessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge((int) (jwtTokenProvider.getValidityInMs() / 1000));
            response.addCookie(accessCookie);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }



}
