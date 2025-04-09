package ua.hudyma.Theater2025.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    public Hall() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hall(Integer id, Integer capacity, String name) {
        this.id = id;
        this.capacity = capacity;
        this.name = name;
    }
}
