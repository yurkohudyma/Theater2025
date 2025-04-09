package ua.hudyma.Theater2025.controller.Rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.constants.UserAccessLevel;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserRepository userRepository;

    @GetMapping
    public List<User> getAll (){
        return userRepository.findAll();
    }

    @GetMapping("{id}")
    public User getById (@PathVariable ("id") Long id){
        return userRepository.findById(id).orElseThrow();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser (@RequestBody User user){
        user.setRegisterDate(LocalDateTime.now());
        user.setAccessLevel(UserAccessLevel.USER);
        userRepository.save(user);
        System.out.println("...added user "+ user.getName());
    }

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
