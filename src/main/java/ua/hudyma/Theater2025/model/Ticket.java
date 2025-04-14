package ua.hudyma.Theater2025.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ua.hudyma.Theater2025.constants.TicketStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Builder
@EqualsAndHashCode(of = "id")
@ToString(/*exclude = {"user"}*/)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    Double value;

    @Column (name = "purchased_on")
    LocalDate purchasedOn;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    //@JsonBackReference(value = "halls_tickets")

    @ManyToOne(optional = false)
    @JoinColumn(name = "hall_id")
    private Hall hall;
    //@JsonBackReference(value = "movies_tickets")

    @ManyToOne(optional = false)
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

    public LocalDate getPurchasedOn() {
        return purchasedOn;
    }

    public void setPurchasedOn(LocalDate purchasedOn) {
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

    public Integer getRoww() {
        return roww;
    }

    public void setRoww(Integer row) {
        this.roww = row;
    }

    public Integer getSeat() {
        return seat;
    }

    public void setSeat(Integer seat) {
        this.seat = seat;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
