package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void updateItem_shouldUpdateName() {
        // Подготовка
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Новое имя");

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Старое имя");
        existingItem.setOwner(new User(userId, "Владелец", "owner@mail.com"));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Действие
        ItemDto updatedItemDto = itemService.updateItem(userId, itemId, itemDto);

        // Проверка
        assertNotNull(updatedItemDto);
        assertEquals("Новое имя", updatedItemDto.getName());
        verify(itemRepository, times(1)).save(existingItem);
    }

    @Test
    void updateItem_shouldUpdateDescription() {
        // Подготовка
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("Новое описание");

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setDescription("Старое описание");
        existingItem.setOwner(new User(userId, "Владелец", "owner@mail.com"));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Действие
        ItemDto updatedItemDto = itemService.updateItem(userId, itemId, itemDto);

        // Проверка
        assertNotNull(updatedItemDto);
        assertEquals("Новое описание", updatedItemDto.getDescription());
        verify(itemRepository, times(1)).save(existingItem);
    }

    @Test
    void updateItem_shouldUpdateAvailable() {
        // Подготовка
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(false);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setAvailable(true);
        existingItem.setOwner(new User(userId, "Владелец", "owner@mail.com"));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Действие
        ItemDto updatedItemDto = itemService.updateItem(userId, itemId, itemDto);

        // Проверка
        assertNotNull(updatedItemDto);
        assertFalse(updatedItemDto.getAvailable());
        verify(itemRepository, times(1)).save(existingItem);
    }

    @Test
    void updateItem_shouldThrowNotFoundExceptionIfItemNotFound() {
        // Подготовка
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Действие и проверка
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(userId, itemId, itemDto);
        });

        assertEquals("Вещь с id=" + itemId + " не найдена", exception.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundExceptionIfUserIsNotOwner() {
        // Подготовка
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setOwner(new User(2L, "Другой владелец", "other@mail.com"));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // Действие и проверка
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(userId, itemId, itemDto);
        });

        assertEquals("Только владелец может редактировать вещь", exception.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }
}