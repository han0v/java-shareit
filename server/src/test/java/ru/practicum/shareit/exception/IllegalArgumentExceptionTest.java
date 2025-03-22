package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IllegalArgumentExceptionTest {

    @Test
    void testIllegalArgumentExceptionWithMessage() {
        // Подготовка
        String errorMessage = "Неверные аргументы";

        // Действие
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Проверка
        assertEquals(errorMessage, exception.getMessage(), "Сообщение исключения должно совпадать с переданным");
    }

    @Test
    void testIllegalArgumentExceptionThrown() {
        // Подготовка
        String errorMessage = "Неверные аргументы";

        // Действие и проверка
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException(errorMessage);
        });

        // Проверка
        assertEquals(errorMessage, exception.getMessage(), "Сообщение исключения должно совпадать с переданным");
    }
}