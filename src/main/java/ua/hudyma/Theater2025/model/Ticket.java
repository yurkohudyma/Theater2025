package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ua.hudyma.Theater2025.constants.TicketStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
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

    @JsonBackReference(value = "users_tickets")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    //@JsonManagedReference(value = "tickets_halls")
    @OneToOne(optional = false)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    //@JsonManagedReference(value = "tickets_movies")
    @OneToOne(optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;






    //get & set

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public LocalDateTime getPurchasedOn() {
        return purchasedOn;
    }

    public void setPurchasedOn(LocalDateTime purchasedOn) {
        this.purchasedOn = purchasedOn;
    }

    public LocalDateTime getScheduledOn() {
        return scheduledOn;
    }

    public void setScheduledOn(LocalDateTime scheduledOn) {
        this.scheduledOn = scheduledOn;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ticket() {
    }

    public Ticket(Long id, Double value, LocalDateTime purchasedOn, LocalDateTime scheduledOn, TicketStatus ticketStatus, Movie movie, User user/*, Hall hall*/) {
        this.id = id;
        this.value = value;
        this.purchasedOn = purchasedOn;
        this.scheduledOn = scheduledOn;
        this.ticketStatus = ticketStatus;
        this.movie = movie;
        this.user = user;
       /* this.hall = hall;*/
    }

}
