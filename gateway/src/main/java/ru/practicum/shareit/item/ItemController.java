package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final ObjectMapper objectMapper;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Добавление предмета: {}", itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Обновление предмета с ID {}: {}", itemId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBookingsDto> getItemById(@PathVariable Long itemId) {
        log.info("Получение предмета по ID: {}", itemId);
        ResponseEntity<Object> response = itemClient.getItemById(itemId);
        return ResponseEntity.status(response.getStatusCode())
                .body(convertResponse(response, ItemWithBookingsDto.class));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получение всех предметов пользователя с ID: {}", userId);
        ResponseEntity<Object> response = itemClient.getAllItemsByOwner(userId);
        return ResponseEntity.status(response.getStatusCode())
                .body(convertResponse(response, new TypeReference<List<ItemDto>>() {
                }));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        log.info("Поиск предметов по тексту: {}", text);
        ResponseEntity<Object> response = itemClient.searchItems(text);
        return ResponseEntity.status(response.getStatusCode())
                .body(convertResponse(response, new TypeReference<List<ItemDto>>() {
                }));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long itemId,
                                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария к предмету с ID {}: {}", itemId, commentDto);
        ResponseEntity<Object> response = itemClient.addComment(userId, itemId, commentDto);
        return ResponseEntity.status(response.getStatusCode())
                .body(convertResponse(response, CommentDto.class));
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