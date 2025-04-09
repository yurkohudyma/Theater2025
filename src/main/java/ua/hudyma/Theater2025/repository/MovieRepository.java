package ua.hudyma.Theater2025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.Theater2025.model.Movie;
import ua.hudyma.Theater2025.model.User;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Transactional(readOnly = true)
    List<Movie>findAll();

    @Transactional(readOnly = true)
    Optional<Movie> findById (Long id);


}
