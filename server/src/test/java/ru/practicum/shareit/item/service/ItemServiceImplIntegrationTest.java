package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.comments.service.CommentRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.Booking.Status.APPROVED;

@DataJpaTest
@Import({ItemServiceImpl.class, UserServiceImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Transactional
    void addItem_withRequest_shouldSaveRequestId() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        User requestor = userRepository.save(createUser("Requestor", "requestor@mail.com"));

        ItemRequest request = new ItemRequest();
        request.setDescription("Need item");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        request = itemRequestRepository.save(request);

        ItemDto dto = new ItemDto();
        dto.setRequestId(request.getId());
        dto.setName("Item");
        dto.setDescription("Desc");
        dto.setAvailable(true);

        ItemDto result = itemService.addItem(owner.getId(), dto);

        // Проверки
        assertNotNull(result.getRequestId());
        assertEquals(request.getId(), result.getRequestId());
        Item savedItem = itemRepository.findById(result.getId()).orElseThrow();
        assertEquals(request.getId(), savedItem.getRequest().getId());
    }

    @Test
    @Transactional
    void updateItem_whenChangeRequest_shouldUpdate() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

        User requestor = userRepository.save(createUser("Requestor", "requestor@mail.com"));
        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("New request");
        newRequest.setRequestor(requestor);
        newRequest.setCreated(LocalDateTime.now());
        newRequest = itemRequestRepository.save(newRequest);

        ItemDto updateDto = new ItemDto();
        updateDto.setRequestId(newRequest.getId());

        ItemDto updated = itemService.updateItem(owner.getId(), item.getId(), updateDto);

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals(newRequest.getId(), updatedItem.getRequest().getId());
    }

    @Test
    @Transactional
    void getItemById_withBookingsAndComments_shouldReturnFullInfo() {
        // Создание пользователей
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        User booker = userRepository.save(createUser("Booker", "booker@mail.com"));

        // Создание предмета
        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

        // Создание бронирований
        Booking pastBooking = createBooking(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item, booker, APPROVED
        );
        bookingRepository.save(pastBooking);

        Booking futureBooking = createBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item, booker, APPROVED
        );
        bookingRepository.save(futureBooking);

        // Создание комментария
        Comment comment = createComment("Great item!", item, booker);
        commentRepository.save(comment);

        // Вызов тестируемого метода
        ItemWithBookingsDto result = itemService.getItemById(item.getId());

        // Проверки
        assertAll(
                () -> assertEquals(pastBooking.getId(), result.getLastBooking().getId()),
                () -> assertEquals(futureBooking.getId(), result.getNextBooking().getId()),
                () -> assertEquals(1, result.getComments().size()),
                () -> assertEquals("Great item!", result.getComments().get(0).getText())
        );
    }


    @Test
    void addComment_whenNoPastBookings_shouldThrow() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        User user = userRepository.save(createUser("User", "user@mail.com"));
        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        assertThrows(ValidationException.class,
                () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item createItem(String name, String desc, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(desc);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end,
                                  Item item, User booker, Booking.Status status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }

    private Comment createComment(String text, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }


}