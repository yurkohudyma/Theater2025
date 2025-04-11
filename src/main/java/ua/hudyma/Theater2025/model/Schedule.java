package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@EqualsAndHashCode(of = "id")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "time_slot")
    String timeSlot;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.EAGER) //lazy throws Could not write JSON: failed to lazily initialize a collection of role: ua.hudyma.Theater2025.model.Hall.hallTicketList: could not initialize proxy - no Session
    @Setter(AccessLevel.PRIVATE)
    private List<Movie> scheduleList = new ArrayList<>();


















    public void addSchedule (Movie movie){
        scheduleList.add(movie);
        movie.setSchedule(this);
    }

    public void removeSchedule (Movie movie){
        scheduleList.remove(movie);
        movie.setSchedule(null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }



}
