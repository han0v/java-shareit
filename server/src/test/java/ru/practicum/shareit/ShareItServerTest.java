package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ShareItServerTest {

    @Test
    void main_shouldRunApplicationWithoutErrors() {
        assertDoesNotThrow(() -> ShareItServer.main(new String[]{}));
    }
}