package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllRequests(userId);
    }
}