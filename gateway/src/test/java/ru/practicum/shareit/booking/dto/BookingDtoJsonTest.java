package ru.practicum.shareit.booking.dto;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 10, 25, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 26, 12, 0);

        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setStatus("APPROVED");

        String jsonContent = objectMapper.writeValueAsString(dto);

        assertThat(jsonContent).contains("\"start\":\"2023-10-25T12:00:00\"");
        assertThat(jsonContent).contains("\"end\":\"2023-10-26T12:00:00\"");
        assertThat(jsonContent).contains("\"status\":\"APPROVED\"");
    }

    @Test
    void deserializeBookingDto() throws Exception {
        String jsonContent = "{\"id\":1,\"start\":\"2023-10-25T12:00:00\"," +
                "\"end\":\"2023-10-26T12:00:00\",\"status\":\"APPROVED\"}";

        BookingDto dto = json.parseObject(jsonContent);

        assertThat(dto.getStart()).isEqualTo("2023-10-25T12:00:00");
        assertThat(dto.getEnd()).isEqualTo("2023-10-26T12:00:00");
        assertThat(dto.getStatus()).isEqualTo("APPROVED");
    }
}