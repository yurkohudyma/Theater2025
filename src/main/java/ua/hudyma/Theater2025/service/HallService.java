package ua.hudyma.Theater2025.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.Theater2025.model.Hall;
import ua.hudyma.Theater2025.repository.HallRepository;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class HallService {
    private final HallRepository hallRepository;

    public List<String> getHallsNames() {
        return hallRepository
                .findAll()
                .stream()
                .map(Hall::getName)
                .toList();
    }


}
