package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        // Находим пользователя, который создает запрос
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        // Создаем объект запроса
        ItemRequest request = ItemRequestMapper.toEntity(itemRequestDto);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        // Сохраняем запрос в базе данных
        ItemRequest savedRequest = itemRequestRepository.save(request);

        // Возвращаем DTO сохраненного запроса
        return ItemRequestMapper.toDto(savedRequest);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        // Находим запрос по ID
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден"));

        // Возвращаем DTO запроса
        return ItemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUser(Long userId) {
        // Находим пользователя
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        // Получаем все запросы пользователя, отсортированные по дате создания
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor);

        // Преобразуем запросы в DTO
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        // Получаем все запросы, созданные другими пользователями, отсортированные по дате создания
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(userId);

        // Преобразуем запросы в DTO
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}