package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";


    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID_HEADER) Long userId, @RequestBody ItemDto itemDto) {
        validateItem(itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("Поле 'name' не может быть пустым.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Поле 'description' не может быть пустым.");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Поле 'available' должно быть указано.");
        }
    }
}