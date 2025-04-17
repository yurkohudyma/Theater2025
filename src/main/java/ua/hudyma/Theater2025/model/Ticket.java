package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.hudyma.Theater2025.constants.TicketStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    Double value;

    @Column (name = "purchased_on")
    LocalDateTime purchasedOn;

    @Column(name = "scheduled_on")
    LocalDateTime scheduledOn;

    @Column(name = "roww")
    Integer roww;

    @Column(name = "seat")
    Integer seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    TicketStatus ticketStatus;

    //@JsonBackReference(value = "users_tickets")
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

   //@JsonBackReference(value = "halls_tickets")
   @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "hall_id")
    private Hall hall;
    //@JsonBackReference(value = "movies_tickets")
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;
}
