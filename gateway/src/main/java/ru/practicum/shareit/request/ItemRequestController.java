package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final ObjectMapper objectMapper;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @Valid @RequestBody ItemRequestCreationDto itemRequestCreationDto) {
        log.info("Создание запроса: {}", itemRequestCreationDto);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(itemRequestCreationDto.getDescription());
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @PathVariable Long requestId) {
        log.info("Получение запроса по ID: {}", requestId);
        ResponseEntity<Object> response = itemRequestClient.getRequestById(userId, requestId);
        return ResponseEntity.status(response.getStatusCode())
                .body(convertResponse(response, ItemRequestDto.class));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllRequestsByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получение всех запросов пользователя с ID: {}", userId);
        ResponseEntity<Object> response = itemRequestClient.getAllRequestsByUser(userId);
        return ResponseEntity.status(response.getStatusCode())
                .body(convertResponse(response, new TypeReference<List<ItemRequestDto>>() {
                }));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получение всех запросов");
        ResponseEntity<Object> response = itemRequestClient.getAllRequests(userId);
        return ResponseEntity.status(response.getStatusCode())
                .body(convertResponse(response, new TypeReference<List<ItemRequestDto>>() {
                }));
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