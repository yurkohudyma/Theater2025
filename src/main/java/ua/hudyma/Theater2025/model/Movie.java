package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ua.hudyma.Theater2025.constants.Genre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
public class Movie {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    Genre genre;

    @Column(name = "premiere_start")
    LocalDate premiereStart;

    @Column(name = "show_end")
    LocalDate showEnd;

    @Column(name = "imdb_index")
    String imdbIndex;

    @Column(name = "name")
    String name;

    @Column(name = "img_url")
    String imgUrl;

    //@JsonManagedReference(value = "movies_tickets")
    @JsonIgnore
    @OneToMany(mappedBy = "movie",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @Setter(AccessLevel.PRIVATE)
    private List<Ticket> movieTicketList = new ArrayList<>();

    //@JsonBackReference(value = "movies_schedules")
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

        //@JsonBackReference(value = "movies_halls")
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", genre=" + genre +
                ", premiereStart=" + premiereStart +
                ", showEnd=" + showEnd +
                ", imdbIndex='" + imdbIndex + '\'' +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", schedule=" + (schedule != null ?
                schedule.getTimeSlot() + "/" +
                        schedule.getTimeSlot2() + "/" +
                        schedule.getTimeSlot3(): "") +
                '}';
    }

}
