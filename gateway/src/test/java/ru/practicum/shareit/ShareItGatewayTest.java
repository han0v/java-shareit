package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ShareItGatewayTest {

    @Test
    void main_shouldRunApplicationWithoutErrors() {
        assertDoesNotThrow(() -> ShareItGateway.main(new String[]{}));
    }
}