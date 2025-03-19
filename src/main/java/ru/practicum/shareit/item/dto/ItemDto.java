package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long requestId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ItemRequestDto request;
}