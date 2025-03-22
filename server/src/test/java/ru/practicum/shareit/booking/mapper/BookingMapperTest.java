package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingMapperTest {

    @Test
    void toDto_shouldMapBookingToBookingDto() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("John Doe");
        booker.setEmail("john@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Booking.Status.WAITING);

        BookingDto bookingDto = BookingMapper.toDto(booking);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getItem()).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingDto.getBooker()).isNotNull();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus().name());
    }

    @Test
    void toEntity_shouldMapBookingDtoToBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setStatus("WAITING");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        bookingDto.setItem(ItemMapper.toDto(item));

        User booker = new User();
        booker.setId(1L);
        booker.setName("John Doe");
        booker.setEmail("john@example.com");
        bookingDto.setBooker(UserMapper.toDto(booker));

        Booking booking = BookingMapper.toEntity(bookingDto);

        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(bookingDto.getId());
        assertThat(booking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(booking.getItem()).isNotNull();
        assertThat(booking.getItem().getId()).isEqualTo(item.getId());
        assertThat(booking.getBooker()).isNotNull();
        assertThat(booking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(booking.getStatus()).isEqualTo(Booking.Status.valueOf(bookingDto.getStatus()));
    }

    @Test
    void getStatusFromString_shouldReturnStatus() {
        String state = "WAITING";
        Booking.Status status = BookingMapper.getStatusFromString(state);
        assertThat(status).isEqualTo(Booking.Status.WAITING);
    }

    @Test
    void getStatusFromString_shouldCatchIllegalArgumentException() {
        String invalidState = "INVALID_STATE";
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            BookingMapper.getStatusFromString(invalidState);
        });
        assertThat(exception.getMessage()).isEqualTo("Unknown state: " + invalidState);
    }

    @Test
    void toEntity_shouldMapBookingDtoToBookingWithNullStatus() {
        // Подготовка
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setStatus(null); // Устанавливаем статус в null

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        bookingDto.setItem(ItemMapper.toDto(item));

        User booker = new User();
        booker.setId(1L);
        booker.setName("John Doe");
        booker.setEmail("john@example.com");
        bookingDto.setBooker(UserMapper.toDto(booker));

        // Действие
        Booking booking = BookingMapper.toEntity(bookingDto);

        // Проверка
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(bookingDto.getId());
        assertThat(booking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(booking.getItem()).isNotNull();
        assertThat(booking.getItem().getId()).isEqualTo(item.getId());
        assertThat(booking.getBooker()).isNotNull();
        assertThat(booking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(booking.getStatus()).isNull(); // Проверяем, что статус остался null
    }
}