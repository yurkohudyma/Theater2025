package ua.hudyma.Theater2025.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.UserAccessLevel;
import ua.hudyma.Theater2025.exception.UserEmailExistsException;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.UserRepository;
import ua.hudyma.Theater2025.security.JwtTokenProvider;

import java.security.Principal;
import java.time.LocalDate;

@Log4j2
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public String login (){
        return "login";
    }

    @PostMapping("/logout")
    public String loginOut (HttpServletResponse response){
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // real env set to true
        cookie.setPath("/");
        cookie.setMaxAge(0); // <== ось це очищає cookie
        response.addCookie(cookie);
        return "redirect:/user";
    }

    @GetMapping("/popup")
    public String popup (){
        return "login-reg_popup";
    }

    @PostMapping("/register")
    public String register(@RequestParam("email") String email,
                           @RequestParam("password") String password,
                           HttpServletResponse response,
                           Model model) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.error(".....Email " + email + " exists");
            throw new UserEmailExistsException("Імейл вже зареєстровано");
        }

        // Створення нового користувача
        User user = new User();
        user.setAccessLevel(UserAccessLevel.USER);
        user.setEmail(email);
        user.setName(email);
        String encodedPass = new BCryptPasswordEncoder().encode(password);
        user.setPassword(encodedPass);
        user.setRegisterDate(LocalDate.now());
        userRepository.save(user);

        log.info("....створено користувача " + user.getEmail());

        // Автентифікація вручну
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER") // або user.getAuthorities() — якщо є
                .build();

        var auth = new UsernamePasswordAuthenticationToken(
                userDetails, user.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Генерація JWT
        String token = jwtTokenProvider.generateToken(email);
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 година
        response.addCookie(cookie);

        return "redirect:/user";
    }


}
