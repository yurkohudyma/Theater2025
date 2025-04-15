package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
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










    //g & s

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserAccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(UserAccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public void addTicket (Ticket ticket){
        userTicketList.add(ticket);
        ticket.setUser(this);
    }

    public void removeTicket (Ticket ticket){
        userTicketList.remove(ticket);
        ticket.setUser(null);
    }

    /*public User() {
    }*/

    /*public User(Long id, String name, String email, UserAccessLevel accessLevel, LocalDateTime registerDate, LocalDateTime updateDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.accessLevel = accessLevel;
        this.registerDate = registerDate;
        this.updateDate = updateDate;
    }*/


}
