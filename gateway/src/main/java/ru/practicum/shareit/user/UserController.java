package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Создание пользователя: {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        log.info("Получение пользователя по ID: {}", userId);
        return ResponseEntity.ok(userClient.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Получение всех пользователей");
        return ResponseEntity.ok(userClient.getAllUsers());
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с ID {}: {}", userId, userDto);
        return ResponseEntity.ok(userClient.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с ID: {}", userId);
        userClient.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private void validateUser(UserDto user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Некорректный email");
        }

        if (userClient.isEmailAlreadyRegistered(user.getEmail())) {
            log.error("Пользователь с email {} уже зарегистрирован", user.getEmail());
            throw new IllegalArgumentException("Пользователь с таким email уже зарегистрирован");
        }
    }

    private void validateEmail(String email) {
        if (email != null && !email.contains("@")) {
            log.error("Некорректный email: {}", email);
            throw new IllegalArgumentException("Некорректный email");
        }
    }
}