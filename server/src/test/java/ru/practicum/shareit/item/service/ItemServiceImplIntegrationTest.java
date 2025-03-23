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
import ru.practicum.shareit.exception.NotFoundException;
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
import java.util.List;

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

        assertNotNull(result.getRequestId());
        assertEquals(request.getId(), result.getRequestId());
        Item savedItem = itemRepository.findById(result.getId()).orElseThrow();
        assertEquals(request.getId(), savedItem.getRequest().getId());
    }

    @Test
    @Transactional
    void addItem_withNonExistentRequest_shouldThrowNotFoundException() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));

        ItemDto dto = new ItemDto();
        dto.setRequestId(999L); // Несуществующий запрос
        dto.setName("Item");
        dto.setDescription("Desc");
        dto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.addItem(owner.getId(), dto));
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
    void updateItem_whenNotOwner_shouldThrowNotFoundException() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        User nonOwner = userRepository.save(createUser("NonOwner", "nonowner@mail.com"));
        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Item");

        assertThrows(NotFoundException.class, () -> itemService.updateItem(nonOwner.getId(), item.getId(), updateDto));
    }

    @Test
    @Transactional
    void getItemById_withBookingsAndComments_shouldReturnFullInfo() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        User booker = userRepository.save(createUser("Booker", "booker@mail.com"));

        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

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

        Comment comment = createComment("Great item!", item, booker);
        commentRepository.save(comment);

        ItemWithBookingsDto result = itemService.getItemById(item.getId());

        assertAll(
                () -> assertEquals(pastBooking.getId(), result.getLastBooking().getId()),
                () -> assertEquals(futureBooking.getId(), result.getNextBooking().getId()),
                () -> assertEquals(1, result.getComments().size()),
                () -> assertEquals("Great item!", result.getComments().get(0).getText())
        );
    }

    @Test
    @Transactional
    void getItemById_withoutBookingsAndComments_shouldReturnBasicInfo() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

        ItemWithBookingsDto result = itemService.getItemById(item.getId());

        assertAll(
                () -> assertNull(result.getLastBooking()),
                () -> assertNull(result.getNextBooking()),
                () -> assertTrue(result.getComments().isEmpty())
        );
    }

    @Test
    @Transactional
    void getAllItemsByOwner_whenNoItems_shouldReturnEmptyList() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));

        List<ItemDto> result = itemService.getAllItemsByOwner(owner.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void getAllItemsByOwner_whenItemsExist_shouldReturnItems() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        itemRepository.save(createItem("Item1", "Desc1", true, owner, null));
        itemRepository.save(createItem("Item2", "Desc2", true, owner, null));

        List<ItemDto> result = itemService.getAllItemsByOwner(owner.getId());

        assertEquals(2, result.size());
    }

    @Test
    @Transactional
    void searchItems_withEmptyText_shouldReturnEmptyList() {
        List<ItemDto> result = itemService.searchItems("");

        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void searchItems_withNonMatchingText_shouldReturnEmptyList() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        itemRepository.save(createItem("Item1", "Desc1", true, owner, null));

        List<ItemDto> result = itemService.searchItems("NonMatchingText");

        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void searchItems_withMatchingText_shouldReturnItems() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        itemRepository.save(createItem("Drill", "Powerful drill", true, owner, null));
        itemRepository.save(createItem("Hammer", "Heavy hammer", true, owner, null));

        List<ItemDto> result = itemService.searchItems("drill");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
    }

    @Test
    @Transactional
    void addComment_whenNoPastBookings_shouldThrow() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        User user = userRepository.save(createUser("User", "user@mail.com"));
        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        assertThrows(ValidationException.class,
                () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    @Transactional
    void addComment_whenPastBookingExists_shouldSaveComment() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        User booker = userRepository.save(createUser("Booker", "booker@mail.com"));
        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

        Booking pastBooking = createBooking(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item, booker, APPROVED
        );
        bookingRepository.save(pastBooking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto result = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertNotNull(result.getId());
        assertEquals("Great item!", result.getText());
    }

    @Test
    @Transactional
    void getCommentsByItemId_whenNoComments_shouldReturnEmptyList() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

        List<CommentDto> result = itemService.getCommentsByItemId(item.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void getCommentsByItemId_whenCommentsExist_shouldReturnComments() {
        User owner = userRepository.save(createUser("Owner", "owner@mail.com"));
        User booker = userRepository.save(createUser("Booker", "booker@mail.com"));
        Item item = itemRepository.save(createItem("Item", "Desc", true, owner, null));

        Comment comment = createComment("Great item!", item, booker);
        commentRepository.save(comment);

        List<CommentDto> result = itemService.getCommentsByItemId(item.getId());

        assertEquals(1, result.size());
        assertEquals("Great item!", result.get(0).getText());
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