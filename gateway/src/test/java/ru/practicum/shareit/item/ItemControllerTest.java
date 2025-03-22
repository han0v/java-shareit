package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
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
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

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
    void getItemById_whenItemExists_returnsItem() throws Exception {
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setId(1L);
        itemWithBookingsDto.setName("Item");
        itemWithBookingsDto.setDescription("Desc");

        when(itemClient.getItemById(anyLong()))
                .thenReturn(ResponseEntity.ok(itemWithBookingsDto));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Desc"));

        verify(itemClient).getItemById(eq(1L));
    }

    @Test
    void getAllItemsByOwner_whenUserExists_returnsItems() throws Exception {
        // Подготовка
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item 1");

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item 2");

        List<ItemDto> items = List.of(item1, item2);

        // Мокируем успешный ответ от itemClient
        when(itemClient.getAllItemsByOwner(anyLong()))
                .thenReturn(ResponseEntity.ok(items));

        // Действие и проверка
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Item 2"));

        // Проверяем, что метод itemClient.getAllItemsByOwner был вызван
        verify(itemClient).getAllItemsByOwner(eq(1L));
    }

    @Test
    void addComment_whenValidData_returnsComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(commentDto));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"));

        verify(itemClient).addComment(eq(1L), eq(1L), any(CommentDto.class));
    }

    @Test
    void addComment_whenInvalidData_returnsBadRequest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("   "); // Пустой текст

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    void convertResponse_whenSuccessfulResponse_returnsConvertedObject() {
        ItemController itemController = new ItemController(itemClient, objectMapper);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");

        ResponseEntity<Object> response = ResponseEntity.ok(itemDto);

        ItemDto result = itemController.convertResponse(response, ItemDto.class);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());
    }

    @Test
    void convertResponse_whenUnsuccessfulResponse_throwsException() {
        ItemController itemController = new ItemController(itemClient, objectMapper);

        ResponseEntity<Object> response = ResponseEntity.badRequest().build();

        assertThrows(RuntimeException.class, () -> {
            itemController.convertResponse(response, ItemDto.class);
        });
    }
}
