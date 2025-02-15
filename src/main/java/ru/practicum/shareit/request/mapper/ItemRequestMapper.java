package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(UserMapper.toDto(request.getRequestor()));
        requestDto.setCreated(request.getCreated());
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