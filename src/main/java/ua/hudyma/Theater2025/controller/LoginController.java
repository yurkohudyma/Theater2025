package ua.hudyma.Theater2025.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.hudyma.Theater2025.constants.UserAccessLevel;
import ua.hudyma.Theater2025.exception.UserEmailExistsException;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.UserRepository;
import ua.hudyma.Theater2025.security.JwtTokenProvider;

import java.time.LocalDate;

@Log4j2
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    public static final String REDIRECT_USER = "redirect:/user";
    public static final String AUTHORIZATION = "Authorization";
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public String login(@RequestParam String email,
                              @RequestParam String password,
                              HttpServletResponse response) {

        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "redirect:/login?error=notfound";
        }

        User user = userOpt.get();
        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return "redirect:/login?error=wrongpassword";
        }

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String token = jwtTokenProvider.generateToken(email);
        Cookie cookie = new Cookie(AUTHORIZATION, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        return REDIRECT_USER;
    }


    @PostMapping("/logout")
    public String loginOut(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTHORIZATION, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // real env set to true
        cookie.setPath("/");
        cookie.setMaxAge(0); // <== ось це очищає cookie
        response.addCookie(cookie);
        return REDIRECT_USER;
    }

    @PostMapping("/register")
    public String register(@RequestParam("email") String email,
                           @RequestParam("password") String password,
                           HttpServletResponse response) {
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
        Cookie cookie = new Cookie(AUTHORIZATION, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 година
        response.addCookie(cookie);

        return REDIRECT_USER;
    }

    @PostMapping("/process-form")
    public String handleForm(@RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String action,
                             HttpServletResponse response) {

        if ("login".equals(action)) {
            return login(email, password, response);
        }
        return register(email, password, response);
    }

}

