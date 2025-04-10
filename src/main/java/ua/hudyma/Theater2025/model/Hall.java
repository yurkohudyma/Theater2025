package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls")
@EqualsAndHashCode(of = "id")
@ToString
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "rowz")
    Integer rowz;

    @Column(name = "seats")
    Integer seats;

    @Column(name = "name")
    String name;

    public Hall() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRowz() {
        return rowz;
    }

    public void setRowz(Integer rows) {
        this.rowz = rows;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonManagedReference(value = "halls_tickets")
    @OneToMany(mappedBy = "hall",
               cascade = CascadeType.ALL,
               fetch = FetchType.EAGER) // lazy throws <Could not write JSON: failed to lazily initialize a collection of role: ua.hudyma.Theater2025.model.Hall.hallTicketList: could not initialize proxy - no Session>
    @Setter(AccessLevel.PRIVATE)
    private List<Ticket> hallTicketList = new ArrayList<>();




    // get & set & construct

    public void addTicket (Ticket ticket){
        hallTicketList.add(ticket);
        ticket.setHall(this);
    }

    public void removeTicket (Ticket ticket){
        hallTicketList.remove(ticket);
        ticket.setHall(null);
    }

}
