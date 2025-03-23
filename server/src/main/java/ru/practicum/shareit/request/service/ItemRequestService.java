package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto); // Создание запроса

    ItemRequestDto getRequestById(Long userId, Long requestId); // Получение запроса по ID

    List<ItemRequestDto> getAllRequestsByUser(Long userId); // Получение всех запросов пользователя

    List<ItemRequestDto> getAllRequests(Long userId); // Получение всех запросов
}