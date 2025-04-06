package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    @NotNull(message = "Описание запроса не может быть пустым")
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}