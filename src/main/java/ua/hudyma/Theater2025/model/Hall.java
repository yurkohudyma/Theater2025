package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "capacity")
    Integer capacity;

    @Column(name = "name")
    String name;

    /*@JsonBackReference(value = "tickets_halls")
    @OneToOne(optional = false)
    @JoinColumn(name = "hall_id")
    private Ticket ticket;*/

    //private List<Movie> movieList;

    public Hall() {
    }

    public Hall(Integer id, Integer capacity, String name/*, Ticket ticket*/) {
        this.id = id;
        this.capacity = capacity;
        this.name = name;
        /*this.ticket = ticket;*/
        /*this.movieList = new ArrayList<>();*/
    }

    /*public void addMovie (Movie movie){
        movieList.add(movie);
    }

    public void removeMovie (Movie movie){
        movieList.remove(movie);
    }*/
}
