package ua.hudyma.Theater2025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket>findAll();

    @Modifying
    @Query("DELETE from Ticket")
    void deleteAllTickets();

    @Transactional
    @Query("delete from Ticket t where t.id = :id")
    @Modifying
    void deleteById (Long id);

    List<Ticket> findByHallIdAndMovieIdAndScheduledOn(
            Long hallId,
            Long movieId,
            LocalDateTime scheduledOn);

    Optional<Ticket> findById(Long id);

    Optional<Ticket> findByOrderId (String orderId);

    boolean existsByUserIdAndTicketStatus(Long userId, TicketStatus status);

    List<Ticket> findByUserIdAndTicketStatus (Long userId, TicketStatus status);

}
