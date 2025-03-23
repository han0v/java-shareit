package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
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
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + bookingDto.getItemId() + " не найдена"));
        if (!item.getAvailable()) {
            throw new RuntimeException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Владелец не может бронировать свою вещь");
        }
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Booking.Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Доступ запрещен");
        }
        LocalDateTime now = LocalDateTime.now();
        if (booking.getEnd().isBefore(now) && booking.getStatus() == Booking.Status.APPROVED) {
            booking.setStatus(Booking.Status.REJECTED);
            bookingRepository.save(booking);
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBefore(user, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfter(user, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerAndStatus(user, Booking.Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerAndStatus(user, Booking.Status.REJECTED);
                break;
            default:
                bookings = bookingRepository.findAllByBookerOrderByStartDesc(user);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
