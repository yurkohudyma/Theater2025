package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

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
    int rowz;

    @Column(name = "seats")
    int seats;

    @Column(name = "name")
    String name;

    @Column(name = "seat_price")
    Double seatPrice;

    //@JsonManagedReference(value = "halls_tickets")
    @OneToMany(mappedBy = "hall",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    // lazy blocks fetching getTicketListSize()
    @Setter(AccessLevel.PRIVATE)
    private List<Ticket> hallTicketList = new ArrayList<>();

    //@JsonManagedReference(value = "movies_halls")
    @OneToMany(mappedBy = "hall",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY) //lazy throws at JSON
    @Setter(AccessLevel.PRIVATE)
    private List<Movie> hallMovieList = new ArrayList<>();

    @OneToMany(mappedBy = "hall",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Seat> seatList = new ArrayList<>();






    public int getTicketListSize() {
        return hallTicketList.size();
    }


    // get & set & construct

    public void addTicket(Ticket ticket) {
        hallTicketList.add(ticket);
        ticket.setHall(this);
    }

    public void removeTicket(Ticket ticket) {
        hallTicketList.remove(ticket);
        ticket.setHall(null);
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

    public Double getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(Double seatPrice) {
        this.seatPrice = seatPrice;
    }
    //boolean[][] seatArray;

    /*@PostLoad
    @PostPersist
    @PostUpdate
    private void initSeatArray() {
        this.seatArray = new boolean[rowz][seats];
    }

    public boolean[][] getSeatArray() {
        return seatArray;
    }

    public void setSeatArray(boolean[][] seatArray) {
        this.seatArray = seatArray;
    }*/

    /*public Hall() {
    }*/

}
