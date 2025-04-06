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
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        ItemRequest request = new ItemRequest();
        request.setDescription(itemRequestDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);
        return ItemRequestMapper.toDto(savedRequest);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден"));
        return ItemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUser(Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor);
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
