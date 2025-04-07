package ua.hudyma.Theater2025.controller.Rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.model.Movie;
import ua.hudyma.Theater2025.repository.MovieRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

@RestController
@RequestMapping("/api/movies")

public class MovieRestController {
    private final MovieRepository movieRepository;

    public MovieRestController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public List<Movie> getAll(){
        return movieRepository.findAll();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addMovie (@RequestBody Movie movie){
        movie.setPremiereStart(LocalDate.now());
        movie.setShowEnd(LocalDate.now().plusDays(7));
        out.println("......adding movie "+ movie.getName());
        movieRepository.save(movie);
    }

    @PostMapping("/addAll")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAll (@RequestBody Movie[] movies){
        Arrays.stream(movies).forEach(this::addMovie);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll (){
        out.println("....deleting all movies");
        movieRepository.findAll().forEach(movieRepository::delete);
    }
}
