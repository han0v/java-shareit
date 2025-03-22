package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequestCreationDto {
    @NotNull(message = "Описание запроса не может быть пустым")
    private String description;
}