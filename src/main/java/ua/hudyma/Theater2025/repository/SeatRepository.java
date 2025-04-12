package ua.hudyma.Theater2025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hudyma.Theater2025.model.Seat;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAll();

    List<Seat> findByHallIdAndIsOccupiedTrue(Integer id);

    @Override
    Optional<Seat> findById(Long id);
}
