package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import ua.hudyma.Theater2025.constants.TicketStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"user"})
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    TicketStatus ticketStatus;

    /*@OneToOne(optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;*/

    public void setUser(User user) {
        this.user = user; //lombok's setter causes build error java: cannot find symbol : method setUser(ua.hudyma.Theater2025.model.User)
    }

    @JsonBackReference(value = "users_tickets")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;

}
