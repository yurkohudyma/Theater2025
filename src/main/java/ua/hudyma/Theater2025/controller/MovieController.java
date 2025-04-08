package ua.hudyma.Theater2025.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.repository.MovieRepository;

@Log4j
@RequestMapping("/movies")
@Controller
public class MovieController {
    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    private final MovieRepository movieRepository;

    @GetMapping("")
    public String getAll (Model model){
        var movieList = movieRepository.findAll();
        model.addAttribute("movieList", movieList);
        return "movies";
    }


}
