package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ItemRequestServiceImpl.class)
class ItemRequestServiceImplTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User(null, "User", "user@mail.com");
        em.persist(user);

        request = new ItemRequest(null, "Нужна дрель", user, LocalDateTime.now());
        em.persist(request);
    }

    @Test
    void createRequest_ValidData_ReturnsItemRequestDto() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен молоток");

        ItemRequestDto result = itemRequestService.createRequest(user.getId(), requestDto);

        assertNotNull(result.getId());
        assertEquals("Нужен молоток", result.getDescription());
        assertEquals(user.getId(), result.getRequestor().getId());
    }

    @Test
    void createRequest_UserNotFound_ThrowsException() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен молоток");

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(999L, requestDto));
    }

    @Test
    void getRequestById_ValidRequest_ReturnsItemRequestDto() {
        ItemRequestDto result = itemRequestService.getRequestById(user.getId(), request.getId());

        assertEquals(request.getId(), result.getId());
        assertEquals("Нужна дрель", result.getDescription());
    }

    @Test
    void getRequestById_RequestNotFound_ThrowsException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(user.getId(), 999L));
    }

    @Test
    void getAllRequestsByUser_ValidUser_ReturnsList() {
        List<ItemRequestDto> result = itemRequestService.getAllRequestsByUser(user.getId());

        assertEquals(1, result.size());
        assertEquals("Нужна дрель", result.get(0).getDescription());
    }

    @Test
    void getAllRequestsByUser_UserNotFound_ThrowsException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequestsByUser(999L));
    }

    @Test
    void getAllRequests_ValidUser_ReturnsList() {
        User anotherUser = new User(null, "Another User", "another@mail.com");
        em.persist(anotherUser);

        ItemRequest anotherRequest = new ItemRequest(null, "Нужен стул", anotherUser, LocalDateTime.now());
        em.persist(anotherRequest);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId());

        assertEquals(1, result.size());
        assertEquals("Нужен стул", result.get(0).getDescription());
    }

    @Test
    void getAllRequests_NoRequests_ReturnsEmptyList() {
        User anotherUser = new User(null, "Another User", "another@mail.com");
        em.persist(anotherUser);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(anotherUser.getId());

        assertTrue(result.isEmpty());
    }
}