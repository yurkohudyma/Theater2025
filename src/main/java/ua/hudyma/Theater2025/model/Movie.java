package ua.hudyma.Theater2025.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import ua.hudyma.Theater2025.constants.Genre;

import java.time.LocalDate;

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

    // get & set & construct

    public Movie() {
    }

    public Movie(Genre genre, LocalDate premiereStart, LocalDate showEnd, String imdbIndex, String name) {
        this.genre = genre;
        this.premiereStart = premiereStart;
        this.showEnd = showEnd;
        this.imdbIndex = imdbIndex;
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
}
