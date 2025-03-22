package ru.practicum.shareit.exceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemController itemController;

    @Test
    void handleNotFoundException() throws Exception {
        when(itemController.getItemById(anyLong()))
                .thenThrow(new NotFoundException("Объект не найден"));

        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void handleValidationException() throws Exception {
        when(itemController.getItemById(anyLong()))
                .thenThrow(new ValidationException("Некорректные данные"));

        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Некорректные данные"));
    }

    @Test
    void handleIllegalArgumentException() throws Exception {
        when(itemController.getItemById(anyLong()))
                .thenThrow(new IllegalArgumentException("Недопустимый аргумент"));

        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Недопустимый аргумент"));
    }

    @Test
    void handleAllExceptions() throws Exception {
        when(itemController.getItemById(anyLong()))
                .thenThrow(new RuntimeException("Непредвиденная ошибка"));

        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Произошла непредвиденная ошибка: Непредвиденная ошибка"));
    }
}