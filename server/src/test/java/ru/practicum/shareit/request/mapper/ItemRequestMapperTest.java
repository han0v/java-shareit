package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    void toDto_shouldMapItemRequestToItemRequestDto() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("John Doe");
        requestor.setEmail("john@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        request.setItems(List.of(item));

        ItemRequestDto requestDto = ItemRequestMapper.toDto(request);

        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getId()).isEqualTo(request.getId());
        assertThat(requestDto.getDescription()).isEqualTo(request.getDescription());
        assertThat(requestDto.getRequestor()).isEqualTo(UserMapper.toDto(requestor));
        assertThat(requestDto.getCreated()).isEqualTo(request.getCreated());
        assertThat(requestDto.getItems()).hasSize(1);
        assertThat(requestDto.getItems().get(0)).isEqualTo(ItemMapper.toDto(item));
    }

    @Test
    void toDto_shouldHandleNullFields() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setRequestor(null);
        request.setCreated(LocalDateTime.now());
        request.setItems(null);

        ItemRequestDto requestDto = ItemRequestMapper.toDto(request);

        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getId()).isEqualTo(request.getId());
        assertThat(requestDto.getDescription()).isEqualTo(request.getDescription());
        assertThat(requestDto.getRequestor()).isNull();
        assertThat(requestDto.getCreated()).isEqualTo(request.getCreated());
        assertThat(requestDto.getItems()).isEmpty();
    }

    @Test
    void toEntity_shouldMapItemRequestDtoToItemRequest() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("John Doe");
        requestor.setEmail("john@example.com");

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Нужна дрель");
        requestDto.setRequestor(UserMapper.toDto(requestor));
        requestDto.setCreated(LocalDateTime.now());

        ItemRequest request = ItemRequestMapper.toEntity(requestDto);

        assertThat(request).isNotNull();
        assertThat(request.getId()).isEqualTo(requestDto.getId());
        assertThat(request.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(request.getRequestor()).isEqualTo(UserMapper.toEntity(requestDto.getRequestor()));
        assertThat(request.getCreated()).isEqualTo(requestDto.getCreated());
    }
}