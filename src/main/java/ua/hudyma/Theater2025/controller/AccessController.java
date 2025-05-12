package ua.hudyma.Theater2025.controller;

/*import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/access")
public class AccessController {

    @GetMapping("/buy")
    public ResponseEntity<?> checkAccessToBuy(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        // Можна вручну перевірити роль
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }


}*/
