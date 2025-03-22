package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collections;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());

        if (request.getRequestor() != null) {
            requestDto.setRequestor(UserMapper.toDto(request.getRequestor()));
        } else {
            requestDto.setRequestor(null);
        }

        requestDto.setCreated(request.getCreated());

        if (request.getItems() != null) {
            requestDto.setItems(request.getItems().stream()
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList()));
        } else {
            requestDto.setItems(Collections.emptyList());
        }

        return requestDto;
    }

    public static ItemRequest toEntity(ItemRequestDto requestDto) {
        ItemRequest request = new ItemRequest();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequestor(UserMapper.toEntity(requestDto.getRequestor()));
        request.setCreated(requestDto.getCreated());
        return request;
    }
}