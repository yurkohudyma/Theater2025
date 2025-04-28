package ua.hudyma.Theater2025.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.UserAccessLevel;
import ua.hudyma.Theater2025.exception.UserEmailExistsException;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.UserRepository;

import java.security.Principal;
import java.time.LocalDate;

@Log4j2
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;

    @GetMapping
    public String login (){
        return "login";
    }

    @GetMapping("/logout")
    public String loginOut (HttpServletResponse response){
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // real env set to true
        cookie.setPath("/");
        cookie.setMaxAge(0); // <== ось це очищає cookie
        response.addCookie(cookie);
        return "redirect:/user";
    }

    /*@PostMapping("/custom-logout")
    public String logoutManually(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/user";
    }*/

    @PostMapping("/register")
    public String register(@RequestParam("email") String email,
                           @RequestParam("password") String password,
                           Model model) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.error(".....Email " + email + " exists");
            throw new UserEmailExistsException("Імейл вже зареєстровано");
        }
        User user = new User();
        user.setAccessLevel(UserAccessLevel.USER);
        user.setEmail(email);
        user.setName(email);
        String encodedPass = new BCryptPasswordEncoder().encode(password);
        user.setPassword(encodedPass);
        user.setRegisterDate(LocalDate.now());
        userRepository.save(user);
        log.info("....створено користувача "+ user.getEmail());
        model.addAttribute("email", email);
        return "redirect:/user";
    }

}
