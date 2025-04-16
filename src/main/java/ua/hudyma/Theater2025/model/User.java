package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.hudyma.Theater2025.constants.UserAccessLevel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table (name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    Long id;

    @Column (name = "name", nullable = false)
    String name;

    @Column (name = "email", unique = true)
    String email;

    @Column(name = "password")
    String password;

    @Enumerated (EnumType.STRING)
    @Column (name = "access_level")
    UserAccessLevel accessLevel;

    @Column(name = "register_date")
    LocalDate registerDate;

    @Column(name = "update_date")
    LocalDate updateDate;

    //@JsonManagedReference(value = "users_tickets")
    @JsonIgnore
    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    @Setter(AccessLevel.PRIVATE)
    private List<Ticket> userTicketList = new ArrayList<>();

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + accessLevel.name());
    }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }

    @Override public boolean isAccountNonExpired() { return true; }

    @Override public boolean isAccountNonLocked() {
        return accessLevel != UserAccessLevel.BLOCKED;
    }

    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override public boolean isEnabled() {
        return accessLevel != UserAccessLevel.BLOCKED;
    }

    public void addTicket (Ticket ticket){
        userTicketList.add(ticket);
        ticket.setUser(this);
    }

    public void removeTicket (Ticket ticket){
        userTicketList.remove(ticket);
        ticket.setUser(null);
    }
}
