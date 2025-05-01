package ua.hudyma.Theater2025.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.Theater2025.service.AuthService;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthService authService;

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
}
