package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "schedules")
@EqualsAndHashCode(of = "id")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "time_slot")
    String timeSlot;

    @Column(name = "time_slot2")
    String timeSlot2;

    @Column(name = "time_slot3")
    String timeSlot3;

    //@JsonManagedReference(value = "movies_schedules")
    @JsonIgnore
    @OneToMany(mappedBy = "schedule",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @Setter(AccessLevel.PRIVATE)
    private List<Movie> scheduleList = new ArrayList<>();
}
