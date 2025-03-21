package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addItem_withValidData_returnsCreated() throws Exception {
        ItemDto requestDto = new ItemDto();
        requestDto.setName("Item");
        requestDto.setDescription("Desc");
        requestDto.setAvailable(true);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Item");
        responseDto.setDescription("Desc");
        responseDto.setAvailable(true);

        when(itemClient.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemClient).addItem(eq(1L), any(ItemDto.class));
    }

    @Test
    void updateItem_withPartialData_shouldPatch() throws Exception {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name");

        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(new ItemDto()));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<ItemDto> updateCaptor = ArgumentCaptor.forClass(ItemDto.class);
        verify(itemClient).updateItem(eq(1L), eq(1L), updateCaptor.capture());

        ItemDto captured = updateCaptor.getValue();
        assertAll(
                () -> assertEquals("New Name", captured.getName()),
                () -> assertNull(captured.getDescription()),
                () -> assertNull(captured.getAvailable())
        );
    }

    @Test
    void searchItems_withSpecialCharacters_shouldEncode() throws Exception {
        // Given
        String searchText = "test item";

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Desc 1");
        item1.setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Desc 2");
        item2.setAvailable(true);

        List<ItemDto> expectedItems = List.of(item1, item2);

        when(itemClient.searchItems(eq(searchText)))
                .thenReturn(ResponseEntity.ok(expectedItems));

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", "test item")) // Передаем декодированный текст
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(itemClient).searchItems(eq(searchText));
    }

    @Test
    void addComment_withInvalidData_returnsBadRequest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("   "); // Пустой текст

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.text").value("Текст комментария не может быть пустым"));

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }
}