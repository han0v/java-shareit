package ru.practicum.shareit.ItemRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerUnitTest {

    @Mock
    private ItemRequestClient itemRequestClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void convertResponse_WithClass_ShouldReturnConvertedObject() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужна дрель");
        ResponseEntity<Object> response = ResponseEntity.ok(itemRequestDto);
        when(objectMapper.convertValue(itemRequestDto, ItemRequestDto.class)).thenReturn(itemRequestDto);
        Method method = ItemRequestController.class.getDeclaredMethod("convertResponse", ResponseEntity.class, Class.class);
        method.setAccessible(true);
        ItemRequestDto result = (ItemRequestDto) method.invoke(itemRequestController, response, ItemRequestDto.class);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Нужна дрель", result.getDescription());
    }

    @Test
    void convertResponse_WithTypeReference_ShouldReturnConvertedObject() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужна дрель");

        ResponseEntity<Object> response = ResponseEntity.ok(List.of(itemRequestDto));

        TypeReference<List<ItemRequestDto>> typeReference = new TypeReference<>() {
        };

        when(objectMapper.convertValue(List.of(itemRequestDto), typeReference)).thenReturn(List.of(itemRequestDto));
        Method method = ItemRequestController.class.getDeclaredMethod("convertResponse", ResponseEntity.class, TypeReference.class);
        method.setAccessible(true);
        List<ItemRequestDto> result = (List<ItemRequestDto>) method.invoke(itemRequestController, response, typeReference);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Нужна дрель", result.get(0).getDescription());
    }

    @Test
    void convertResponse_WithTypeReference_ShouldThrowExceptionIfResponseIsNotSuccessful() throws Exception {
        // Подготовка
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        // Используем рефлексию для вызова приватного метода
        Method method = ItemRequestController.class.getDeclaredMethod("convertResponse", ResponseEntity.class, TypeReference.class);
        method.setAccessible(true);

        // Действие и проверка
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(itemRequestController, response, new TypeReference<List<ItemRequestDto>>() {
            });
        });

        // Проверяем, что исключение является InvocationTargetException
        assertTrue(exception instanceof InvocationTargetException);

        // Извлекаем исходное исключение
        Throwable cause = ((InvocationTargetException) exception).getTargetException();

        // Проверяем тип и сообщение исходного исключения
        assertTrue(cause instanceof RuntimeException);
        assertEquals("Failed to convert response to java.util.List<ru.practicum.shareit.request.dto.ItemRequestDto>", cause.getMessage());
    }
}