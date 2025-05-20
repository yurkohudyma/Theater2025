package ua.hudyma.Theater2025.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.Theater2025.model.Movie;
import ua.hudyma.Theater2025.model.Schedule;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie>findAll();

    /*@EntityGraph(attributePaths = "schedule")
    List<Movie>findAll();*/

    /*@Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.schedule")
    List<Movie> findAllWithSchedules();*/

    Optional<Movie> findById (Long id);


}
