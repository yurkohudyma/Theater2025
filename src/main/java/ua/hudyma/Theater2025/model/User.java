package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ua.hudyma.Theater2025.constants.UserAccessLevel;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "users")
@Data
@EqualsAndHashCode(of = "id")
@ToString
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    Long id;

    @Column (name = "name", nullable = false)
    String name;

    @Column (name = "email", unique = true)
    String email;

    @Enumerated (EnumType.STRING)
    @Column (name = "access_level")
    UserAccessLevel accessLevel;

    @Column(name = "register_date")
    LocalDateTime registerDate;

    @Column(name = "update_date")
    LocalDateTime updateDate;

    @JsonManagedReference(value = "users_tickets")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Setter(AccessLevel.PRIVATE)
    private List<Ticket> ticketList;

    public void addTicket (Ticket ticket){
        ticketList.add(ticket);
        ticket.setUser(this);
    }

    private void removeTicket (Ticket ticket){
        ticketList.remove(ticket);
        ticket.setUser(null);
    }




    //todo transactions list

    //todo methods addTicket etc CRUD

}
