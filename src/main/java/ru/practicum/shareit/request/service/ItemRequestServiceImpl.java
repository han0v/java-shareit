package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    //не стал разрабатывать функционал работы с бд для ItenRequest ибо про это ничего в тз не сказано
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден")); // Обработка отсутствия пользователя
        ItemRequest request = ItemRequestMapper.toEntity(itemRequestDto);
        request.setRequestor(requestor);
        ItemRequest savedRequest = itemRequestRepository.save(request);
        return ItemRequestMapper.toDto(savedRequest);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId);
        return ItemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUser(Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден")); // Обработка отсутствия пользователя
        return itemRequestRepository.findAllByRequestor(requestor).stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        return itemRequestRepository.findAll().values().stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}