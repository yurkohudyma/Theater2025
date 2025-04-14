package ua.hudyma.Theater2025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.repository.MovieRepository;
import ua.hudyma.Theater2025.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    public UserController(UserRepository userRepository, MovieRepository movieRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public String getAllMovies (Model model){
        var moviesList = movieRepository.findAll();
        model.addAttribute("moviesList", moviesList);
        return "user";
    }
}
