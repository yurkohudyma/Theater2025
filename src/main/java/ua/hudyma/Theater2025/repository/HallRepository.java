package ua.hudyma.Theater2025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.Theater2025.model.Hall;

import java.util.List;

@Repository
public interface HallRepository extends JpaRepository<Hall, Integer> {
    @Transactional
    List<Hall>findAll();
}
