package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void createBooking() throws Exception {
        // Подготовка данных
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        // Проверка вызова метода сервиса
        verify(bookingService).createBooking(eq(1L), any(BookingDto.class));
    }

    @Test
    void approveBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus("APPROVED");

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).approveBooking(eq(1L), eq(1L), eq(true));
    }

    @Test
    void getBookingById() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService).getBookingById(eq(1L), eq(1L));
    }

    @Test
    void getAllBookingsByOwner() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        List<BookingDto> bookings = Collections.singletonList(bookingDto);

        when(bookingService.getAllBookingsByOwner(anyLong(), anyString()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings?state=ALL")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bookingService).getAllBookingsByOwner(eq(1L), eq("ALL"));
    }
}