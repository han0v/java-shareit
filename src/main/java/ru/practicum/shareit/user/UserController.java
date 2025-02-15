package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.IllegalArgumentException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        validateUser(userDto);
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto existingUser = userService.getUserById(userId);


        if (!existingUser.getEmail().equals(userDto.getEmail())) {
            if (userService.isEmailAlreadyRegistered(userDto.getEmail())) {
                log.error("Пользователь с email {} уже зарегистрирован", userDto.getEmail());
                throw new IllegalArgumentException("Пользователь с таким email уже зарегистрирован");
            }
        }


        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        return userService.updateUser(userId, existingUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    private void validateUser(UserDto user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Некорректный email: {}", user.getEmail());
            throw new ValidationException("Некорректный email");
        }

        if (userService.isEmailAlreadyRegistered(user.getEmail())) {
            log.error("Пользователь с email {} уже зарегистрирован", user.getEmail());
            throw new IllegalArgumentException("Пользователь с таким email уже зарегистрирован");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            log.error("Некорректный email: {}", email);
            throw new IllegalArgumentException("Некорректный email");
        }
    }
}