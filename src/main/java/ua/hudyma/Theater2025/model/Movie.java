package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import ua.hudyma.Theater2025.constants.Genre;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Movie {

    public Movie() {
    }

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

   /* @OneToOne(mappedBy = "movie")
    Ticket ticket;

    @JsonBackReference(value = "halls_movies")
    @ManyToOne(optional = true)
    @JoinColumn(name = "movie_id")
    private Hall hall;*/



    public Movie(Long id, Genre genre, LocalDate premiereStart, LocalDate showEnd, String imdbIndex/*, Ticket ticket, Hall hall*/, String name) {
        this.id = id;
        this.genre = genre;
        this.premiereStart = premiereStart;
        this.showEnd = showEnd;
        this.imdbIndex = imdbIndex;
        /*this.ticket = ticket;
        this.hall = hall;*/
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public LocalDate getPremiereStart() {
        return premiereStart;
    }

    public void setPremiereStart(LocalDate premiereStart) {
        this.premiereStart = premiereStart;
    }

    public LocalDate getShowEnd() {
        return showEnd;
    }

    public void setShowEnd(LocalDate showEnd) {
        this.showEnd = showEnd;
    }

    public String getImdbIndex() {
        return imdbIndex;
    }

    public void setImdbIndex(String imdbIndex) {
        this.imdbIndex = imdbIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }*/

    /*public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }*/
}
