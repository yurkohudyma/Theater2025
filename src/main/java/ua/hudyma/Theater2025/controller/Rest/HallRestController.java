package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.dto.HallDTO;
import ua.hudyma.Theater2025.model.Hall;
import ua.hudyma.Theater2025.repository.HallRepository;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

@RestController
@RequestMapping("/api/halls")
public class HallRestController {

    private final HallRepository hallRepository;

    public HallRestController(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @GetMapping("/all")
    public List<Hall> getAll() {
        return hallRepository.findAll();
    }

    @GetMapping
    public List<HallDTO> getAllDto (){
        return hallRepository
                .findAll()
                .stream()
                .map(HallDTO::from)
                .toList();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHall(@RequestBody Hall hall) {
        out.println("......adding hall " + hall.getName());
        hallRepository.save(hall);
    }

    @PostMapping("/addAll")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAll (@RequestBody Hall[] halls){
        Arrays.stream(halls).forEach(this::addHall);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll (){
        out.println("....deleting all halls");
        hallRepository.findAll().forEach(hallRepository::delete);
    }
}
