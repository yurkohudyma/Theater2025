package ua.hudyma.Theater2025.dto;

import ua.hudyma.Theater2025.constants.UserAccessLevel;
import ua.hudyma.Theater2025.model.User;

import java.time.LocalDate;

public record UserDTO(Long id, String name, String email, UserAccessLevel accessLevel, LocalDate registerDate) {
    public static UserDTO from(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAccessLevel(),
                user.getRegisterDate()
        );
    }
}

