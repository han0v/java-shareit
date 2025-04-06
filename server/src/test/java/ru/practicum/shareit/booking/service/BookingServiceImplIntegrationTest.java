package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.Booking.Status.*;

@SpringBootTest
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        userRepository.save(booker);

        item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);
    }

    @Test
    void createBooking_ValidData_ReturnsBookingDto() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        BookingDto result = bookingService.createBooking(booker.getId(), bookingDto);

        assertNotNull(result.getId());
        assertEquals(WAITING.name(), result.getStatus());
    }

    @Test
    void createBooking_WithNonExistentUser_ThrowsNotFoundException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(999L, bookingDto));
    }

    @Test
    void createBooking_WithNonExistentItem_ThrowsNotFoundException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(999L);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(booker.getId(), bookingDto));
    }

    @Test
    void createBooking_WithUnavailableItem_ThrowsRuntimeException() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(booker.getId(), bookingDto));
    }

    @Test
    void createBooking_ByOwner_ThrowsRuntimeException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(owner.getId(), bookingDto));
    }

    @Test
    void approveBooking_ValidApproval_UpdatesStatus() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);

        BookingDto result = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertEquals(APPROVED.name(), result.getStatus());
    }

    @Test
    void approveBooking_ByNonOwner_ThrowsRuntimeException() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);

        assertThrows(RuntimeException.class, () -> bookingService.approveBooking(booker.getId(), booking.getId(), true));
    }

    @Test
    void approveBooking_AlreadyProcessed_ThrowsRuntimeException() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        assertThrows(RuntimeException.class, () -> bookingService.approveBooking(owner.getId(), booking.getId(), true));
    }

    @Test
    void getBookingById_ValidData_ReturnsBookingDto() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);

        BookingDto result = bookingService.getBookingById(booker.getId(), booking.getId());

        assertNotNull(result.getId());
        assertEquals(WAITING.name(), result.getStatus());
    }

    @Test
    void getBookingById_ByNonOwnerAndNonBooker_ThrowsRuntimeException() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);

        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@mail.com");
        userRepository.save(anotherUser);

        assertThrows(RuntimeException.class, () -> bookingService.getBookingById(anotherUser.getId(), booking.getId()));
    }

    @Test
    void getBookingById_WithExpiredBooking_UpdatesStatus() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        BookingDto result = bookingService.getBookingById(booker.getId(), booking.getId());

        assertEquals(REJECTED.name(), result.getStatus());
    }

    @Test
    void getAllBookingsByOwner_WithStateCurrent_ReturnsCurrentBookings() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(booker.getId(), BookingState.CURRENT);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllBookingsByOwner_WithStatePast_ReturnsPastBookings() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(booker.getId(), BookingState.PAST);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllBookingsByOwner_WithStateFuture_ReturnsFutureBookings() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(booker.getId(), BookingState.FUTURE);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllBookingsByOwner_WithStateWaiting_ReturnsWaitingBookings() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(booker.getId(), BookingState.WAITING);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllBookingsByOwner_WithStateRejected_ReturnsRejectedBookings() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(REJECTED);
        bookingRepository.save(booking);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(booker.getId(), BookingState.REJECTED);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllBookingsByOwner_WithStateAll_ReturnsAllBookings() {
        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(APPROVED);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusDays(3));
        booking2.setEnd(LocalDateTime.now().plusDays(4));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(REJECTED);
        bookingRepository.save(booking2);

        List<BookingDto> result = bookingService.getAllBookingsByOwner(booker.getId(), BookingState.ALL);

        assertEquals(2, result.size());
    }
}