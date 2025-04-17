package ua.hudyma.Theater2025.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.Theater2025.constants.UserAccessLevel;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    public void blockUser (User user){
        if (user.getAccessLevel() == UserAccessLevel.BLOCKED){
            log.error("...User is already BLOCKED");
            return;
        }
        log.info("...User " + user.getEmail() + "  has been BLOCKED");
        user.setAccessLevel(UserAccessLevel.BLOCKED);
    }
}
