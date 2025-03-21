package ru.practicum.shareit.ItemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void createRequest_ValidData_ReturnsOk() throws Exception {
        ItemRequestCreationDto requestDto = new ItemRequestCreationDto();
        requestDto.setDescription("Нужна дрель");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Нужна дрель\"}"))
                .andExpect(status().isOk());

        verify(itemRequestClient).createRequest(eq(1L), any());
    }

    @Test
    void getRequestById_ValidRequest_ReturnsOk() throws Exception {
        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужна дрель");
        responseDto.setCreated(LocalDateTime.now());

        when(itemRequestClient.getRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void getAllRequestsByUser_ValidUser_ReturnsOk() throws Exception {
        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужна дрель");
        responseDto.setCreated(LocalDateTime.now());

        // Исправлено: возвращаем ResponseEntity<List<ItemRequestDto>>
        when(itemRequestClient.getAllRequestsByUser(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(responseDto)));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

    @Test
    void getAllRequests_ValidUser_ReturnsOk() throws Exception {
        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужна дрель");
        responseDto.setCreated(LocalDateTime.now());

        when(itemRequestClient.getAllRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(responseDto)));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }
}