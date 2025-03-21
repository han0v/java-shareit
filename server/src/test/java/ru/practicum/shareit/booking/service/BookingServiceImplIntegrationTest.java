package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertEquals(Booking.Status.WAITING.name(), result.getStatus());
    }

    @Test
    void approveBooking_ValidApproval_UpdatesStatus() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Booking.Status.WAITING);
        bookingRepository.save(booking);

        BookingDto result = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertEquals(Booking.Status.APPROVED.name(), result.getStatus());
    }
}
