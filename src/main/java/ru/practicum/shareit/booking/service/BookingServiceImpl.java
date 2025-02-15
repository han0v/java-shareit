package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId);
        Item item = itemRepository.findById(bookingDto.getItem().getId());
        if (!item.getAvailable()) {
            throw new RuntimeException("Вещь недоступна для бронирования");
        }
        Booking booking = BookingMapper.toEntity(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Booking.Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Только владелец может подтвердить бронирование");
        }
        if (booking.getStatus() != Booking.Status.WAITING) {
            throw new RuntimeException("Бронирование уже обработано");
        }
        booking.setStatus(approved ? Booking.Status.APPROVED : Booking.Status.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Доступ запрещен");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByUser(Long userId, String state) {
        User user = userRepository.findById(userId);
        if (!List.of("WAITING", "APPROVED", "REJECTED", "CANCELED").contains(state)) {
            throw new RuntimeException("Unknown state: " + state);
        }

        List<Booking> bookings = bookingRepository.findAllByBookerAndStatus(user, Booking.Status.valueOf(state));

        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}