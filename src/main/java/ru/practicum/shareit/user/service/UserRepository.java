package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        if (users.get(id) == null) {
            throw new NotFoundException("Пользователя не сущетсвует");
        }
        return users.get(id);
    }

    public Map<Long, User> findAll() {
        return new HashMap<>(users);
    }

    public void deleteById(Long id) {
        users.remove(id);
    }


}