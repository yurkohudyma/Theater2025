package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import ua.hudyma.Theater2025.constants.Genre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
@EqualsAndHashCode(of = "id")
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

    @JsonManagedReference(value = "movies_tickets")
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.EAGER) //lazy throws Could not write JSON: failed to lazily initialize a collection of role: ua.hudyma.Theater2025.model.Hall.hallTicketList: could not initialize proxy - no Session
    @Setter(AccessLevel.PRIVATE)
    private List<Ticket> movieTicketList = new ArrayList<>();



    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;


    @JsonBackReference(value = "movies_schedules")
    @OneToOne
    private Schedule schedule2;
















    // get & set & construct

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }


    public void addTicket (Ticket ticket){
        movieTicketList.add(ticket);
        ticket.setMovie(this);
    }

    public void removeTicket (Ticket ticket){
        movieTicketList.remove(ticket);
        ticket.setMovie(null);
    }

    public Movie() {
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
}
