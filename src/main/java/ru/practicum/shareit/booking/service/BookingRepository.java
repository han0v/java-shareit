package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Booking.Status;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class BookingRepository {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private long nextId = 1;

    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(nextId++);
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public Booking findById(Long id) {
        return bookings.get(id);
    }

    public Map<Long, Booking> findAll() {
        return new HashMap<>(bookings);
    }

    public void deleteById(Long id) {
        bookings.remove(id);
    }

    public List<Booking> findAllByBookerAndStatus(User booker, Status status) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker().equals(booker)) // Фильтруем по пользователю
                .filter(booking -> booking.getStatus() == status) // Фильтруем по статусу
                .collect(Collectors.toList());
    }
}