package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDto bookingDto);

    BookingDto approveBooking(Long userId, Long bookingId, boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsByUser(Long userId, Booking.Status state);
}