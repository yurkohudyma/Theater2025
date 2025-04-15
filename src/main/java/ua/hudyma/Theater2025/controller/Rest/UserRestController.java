package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.UserAccessLevel;
import ua.hudyma.Theater2025.dto.UserDTO;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserRepository userRepository;

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<User> getAll (){
        System.out.println("......... current auth: " + SecurityContextHolder.getContext().getAuthentication());
        return userRepository.findAll();
    }

    @GetMapping
    public List<UserDTO> getAllUsersDto (){
        return userRepository.findAll().stream().map(UserDTO::from).toList();
    }

    @GetMapping("{id}")
    public User getById (@PathVariable ("id") Long id){
        return userRepository.findById(id).orElseThrow();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser (@RequestBody User user){
        user.setRegisterDate(LocalDate.now());
        user.setAccessLevel(UserAccessLevel.USER);
        userRepository.save(user);
        System.out.println("...added user "+ user.getName());
    }

    @PostMapping("/addAll")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAll (@RequestBody User[] users){
        Arrays.stream(users).forEach(this::addUser);
    }




}
