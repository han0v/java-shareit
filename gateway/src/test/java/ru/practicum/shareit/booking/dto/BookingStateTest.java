package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingStateTest {

    @Test
    void from_shouldReturnCorrectStateForValidString() {
        // Проверяем корректное преобразование для каждого значения BookingState
        assertEquals(Optional.of(BookingState.ALL), BookingState.from("ALL"));
        assertEquals(Optional.of(BookingState.CURRENT), BookingState.from("CURRENT"));
        assertEquals(Optional.of(BookingState.FUTURE), BookingState.from("FUTURE"));
        assertEquals(Optional.of(BookingState.PAST), BookingState.from("PAST"));
        assertEquals(Optional.of(BookingState.REJECTED), BookingState.from("REJECTED"));
        assertEquals(Optional.of(BookingState.WAITING), BookingState.from("WAITING"));

        // Проверяем регистронезависимость
        assertEquals(Optional.of(BookingState.ALL), BookingState.from("all"));
        assertEquals(Optional.of(BookingState.CURRENT), BookingState.from("current"));
        assertEquals(Optional.of(BookingState.FUTURE), BookingState.from("future"));
        assertEquals(Optional.of(BookingState.PAST), BookingState.from("past"));
        assertEquals(Optional.of(BookingState.REJECTED), BookingState.from("rejected"));
        assertEquals(Optional.of(BookingState.WAITING), BookingState.from("waiting"));
    }

    @Test
    void from_shouldReturnEmptyForInvalidString() {
        // Проверяем, что метод возвращает Optional.empty() для некорректных строк
        assertEquals(Optional.empty(), BookingState.from("INVALID_STATE"));
        assertEquals(Optional.empty(), BookingState.from(""));
        assertEquals(Optional.empty(), BookingState.from(null));
    }
}