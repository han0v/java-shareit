package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    void serializeItemDto() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Аккумуляторная дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(100L);

        JsonContent<ItemDto> json = jacksonTester.write(itemDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo("Аккумуляторная дрель");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(json).extractingJsonPathNumberValue("$.requestId").isEqualTo(100);
    }

    @Test
    void deserializeItemDto() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Дрель\"," +
                "\"description\":\"Аккумуляторная дрель\"," +
                "\"available\":true,\"requestId\":100}";

        ItemDto itemDto = jacksonTester.parseObject(jsonContent);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Дрель");
        assertThat(itemDto.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(100L);
    }
}