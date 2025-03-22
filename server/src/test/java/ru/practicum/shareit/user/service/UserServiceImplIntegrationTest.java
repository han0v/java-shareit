package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserServiceImpl.class)
class UserServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUserById_whenUserExists_thenReturnUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = em.persistAndFlush(user);

        UserDto result = userService.getUserById(savedUser.getId());

        assertEquals(savedUser.getId(), result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void updateUser_whenEmailChangedToExisting_shouldThrow() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        em.persistAndFlush(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        User savedUser2 = em.persistAndFlush(user2);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("user1@example.com");
        updateDto.setName("User2 Updated"); // Убедимся, что имя также установлено

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(savedUser2.getId(), updateDto));
    }

    @Test
    void createUser_whenEmailDuplicate_shouldThrow() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        userService.createUser(userDto);

        UserDto duplicateDto = new UserDto();
        duplicateDto.setName("Another User");
        duplicateDto.setEmail("test@example.com");

        assertThrows(RuntimeException.class,
                () -> userService.createUser(duplicateDto));
    }

    @Test
    void getAllUsers_whenNoUsers_returnsEmptyList() {
        List<UserDto> result = userService.getAllUsers();
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUser_whenUserExists_shouldRemoveFromDb() {
        User user = em.persistAndFlush(new User(null, "DelUser", "del@mail.com"));

        userService.deleteUser(user.getId());

        assertFalse(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    void updateUser_whenOnlyNameChanged_shouldUpdateName() {
        User existing = em.persistAndFlush(new User(null, "OldName", "name@mail.com"));
        UserDto updateDto = new UserDto();
        updateDto.setName("NewName");

        UserDto updated = userService.updateUser(existing.getId(), updateDto);

        assertEquals("NewName", updated.getName());
        assertEquals("name@mail.com", updated.getEmail());
    }

    @Test
    void getUserById_whenUserNotExists_shouldThrow() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void isEmailAlreadyRegistered_whenEmailExists_returnsTrue() {
        em.persistAndFlush(new User(null, "Test", "exist@mail.com"));
        assertTrue(userService.isEmailAlreadyRegistered("exist@mail.com"));
    }
}