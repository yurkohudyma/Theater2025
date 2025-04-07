package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

   /* @JsonManagedReference(value = "halls_movies")
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Setter(AccessLevel.PRIVATE)
    private List<Movie> movieList = new ArrayList<>();
*/


}
