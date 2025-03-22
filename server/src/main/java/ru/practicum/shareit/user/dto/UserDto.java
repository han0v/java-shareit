package ru.practicum.shareit.user.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;

@Data
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "Некорректный формат email")
    private String email;
}
