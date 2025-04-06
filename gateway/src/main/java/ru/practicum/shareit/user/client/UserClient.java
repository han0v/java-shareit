package ru.practicum.shareit.user.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";
    private final ObjectMapper objectMapper;
    //не стал изменять BaseClient, добавил ObjectMapper для преобразования тела ответа

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder, ObjectMapper objectMapper) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("", userDto);
    }

    public UserDto getUserById(Long userId) {
        ResponseEntity<Object> response = get("/" + userId);
        return convertResponse(response, UserDto.class);
    }

    public List<UserDto> getAllUsers() {
        ResponseEntity<Object> response = get("");
        return convertResponse(response, new TypeReference<List<UserDto>>() {
        });
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        ResponseEntity<Object> response = patch("/" + userId, userDto);
        return convertResponse(response, UserDto.class);
    }

    public void deleteUser(Long userId) {
        delete("/" + userId);
    }

    public boolean isEmailAlreadyRegistered(String email) {
        Map<String, Object> parameters = Map.of("email", email);
        ResponseEntity<Object> response = get("/check-email?email={email}", null, parameters);
        return convertResponse(response, Boolean.class);
    }

    private <T> T convertResponse(ResponseEntity<Object> response, Class<T> clazz) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return objectMapper.convertValue(response.getBody(), clazz);
        }
        throw new RuntimeException("Failed to convert response to " + clazz.getSimpleName());
    }

    private <T> T convertResponse(ResponseEntity<Object> response, TypeReference<T> typeReference) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return objectMapper.convertValue(response.getBody(), typeReference);
        }
        throw new RuntimeException("Failed to convert response to " + typeReference.getType().getTypeName());
    }
}