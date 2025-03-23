package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(ItemMapper.toDto(booking.getItem())); // Полный объект ItemDto
        bookingDto.setBooker(UserMapper.toDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus().name());
        return bookingDto;
    }

    public Booking toEntity(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        Item item = ItemMapper.toEntity(bookingDto.getItem());
        booking.setItem(item);
        User booker = UserMapper.toEntity(bookingDto.getBooker());
        booking.setBooker(booker);
        if (bookingDto.getStatus() != null) {
            booking.setStatus(Booking.Status.valueOf(bookingDto.getStatus()));
        }
        return booking;
    }

    public Booking.Status getStatusFromString(String state) {
        try {
            return Booking.Status.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown state: " + state);
        }
    }
}
