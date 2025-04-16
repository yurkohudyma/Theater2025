package ua.hudyma.Theater2025.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.UserAccessLevel;
import ua.hudyma.Theater2025.exception.UserEmailExistsException;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.UserRepository;
import util.PassGen;

import java.time.LocalDate;

@Log4j
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;

    @GetMapping
    public String login (){
        return "login";
    }

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
