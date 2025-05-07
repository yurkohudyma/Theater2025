package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls")
@EqualsAndHashCode(of = "id")
@Data
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
    @JsonIgnore
    @OneToMany(mappedBy = "hall",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @Setter(AccessLevel.PRIVATE)
    private List<Ticket> hallTicketList = new ArrayList<>();

    //@JsonManagedReference(value = "movies_halls")
    @JsonIgnore
    @OneToMany(mappedBy = "hall",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @Setter(AccessLevel.PRIVATE)
    private List<Movie> hallMovieList = new ArrayList<>();

    //@JsonManagedReference(value = "halls_seats")
    @JsonIgnore
    @OneToMany(mappedBy = "hall",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private List<Seat> seatList = new ArrayList<>();

    @Override
    public String toString() {
        return "Hall{" +
                "id=" + id +
                ", rowz=" + rowz +
                ", seats=" + seats +
                ", name='" + name + '\'' +
                ", seatPrice=" + seatPrice +
                '}';
    }

    public int getTicketListSize() {
        return hallTicketList.size();
    }
}
