package ua.hudyma.Theater2025.repository;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.Theater2025.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Transactional
    List<Ticket>findAll();

    @Transactional
    @Modifying
    @Query("DELETE from Ticket")
    void deleteAllTickets();

    @Query("delete from Ticket t where t.id = :id")
    @Modifying
    void deleteById (Long id);

    List<Ticket> findByHallIdAndMovieIdAndScheduledOn(
            Long hallId,
            Long movieId,
            LocalDateTime scheduledOn);
}
