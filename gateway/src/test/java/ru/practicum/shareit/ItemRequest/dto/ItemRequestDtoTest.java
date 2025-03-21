package ru.practicum.shareit.ItemRequest.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void serializeItemRequestDto() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setCreated(LocalDateTime.of(2023, 10, 25, 12, 0));

        JsonContent<ItemRequestDto> jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-25T12:00:00");
    }

    @Test
    void deserializeItemRequestDto() throws Exception {
        String jsonContent = "{\"id\":1,\"description\":\"Нужна дрель\",\"created\":\"2023-10-25T12:00:00\"}";

        ItemRequestDto dto = json.parseObject(jsonContent);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 25, 12, 0));
    }

}