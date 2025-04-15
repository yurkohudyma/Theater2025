package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.Theater2025.dto.SeatDTO;
import ua.hudyma.Theater2025.repository.SeatRepository;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatRestController {

    public SeatRestController(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }
    private final SeatRepository seatRepository;

    @GetMapping
    public List<SeatDTO> getAll (){
        return seatRepository
                .findAll()
                .stream()
                .map(SeatDTO::from)
                .toList();
    }

}
