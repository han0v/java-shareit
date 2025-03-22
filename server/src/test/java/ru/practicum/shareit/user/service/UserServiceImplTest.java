package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void updateUser_shouldUpdateName() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Новое имя");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Старое имя");
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updatedUserDto = userService.updateUser(userId, userDto);

        assertNotNull(updatedUserDto);
        assertEquals("Новое имя", updatedUserDto.getName());
        assertEquals("old@example.com", updatedUserDto.getEmail()); // Email не изменился
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_shouldUpdateEmail() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setEmail("new@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Старое имя");
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updatedUserDto = userService.updateUser(userId, userDto);

        assertNotNull(updatedUserDto);
        assertEquals("new@example.com", updatedUserDto.getEmail());
        assertEquals("Старое имя", updatedUserDto.getName()); // Имя не изменилось
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_shouldThrowExceptionIfEmailAlreadyRegistered() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setEmail("new@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Старое имя");
        existingUser.setEmail("old@example.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(anotherUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, userDto);
        });

        assertEquals("Пользователь с таким email уже зарегистрирован", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldNotUpdateIfNoChanges() {
        Long userId = 1L;
        UserDto userDto = new UserDto();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Старое имя");
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updatedUserDto = userService.updateUser(userId, userDto);

        assertNotNull(updatedUserDto);
        assertEquals("Старое имя", updatedUserDto.getName());
        assertEquals("old@example.com", updatedUserDto.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_shouldThrowExceptionIfUserNotFound() {
        Long userId = 1L;
        UserDto userDto = new UserDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.updateUser(userId, userDto);
        });

        assertEquals("Пользователь с id=" + userId + " не найден", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}