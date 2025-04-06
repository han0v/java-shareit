package ru.practicum.shareit.comments.dto;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.comments.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    private final Validator validator;

    @Autowired
    private JacksonTester<CommentDto> json;

    public CommentDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialization() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setAuthorName("Author");
        comment.setCreated(LocalDateTime.parse("2023-10-01T12:00:00"));

        assertThat(json.write(comment))
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json.write(comment))
                .extractingJsonPathStringValue("$.text").isEqualTo("Test comment");
        assertThat(json.write(comment))
                .extractingJsonPathStringValue("$.authorName").isEqualTo("Author");
        assertThat(json.write(comment))
                .extractingJsonPathStringValue("$.created").isEqualTo("2023-10-01T12:00:00");
    }

    @Test
    void shouldValidateNotBlankConstraint() {
        CommentDto comment = new CommentDto();
        comment.setText(" "); // Пробелы вместо текста

        var violations = validator.validate(comment);
        assertThat(violations)
                .hasSize(1)
                .extracting("message")
                .containsExactly("Текст комментария не может быть пустым");
    }

    @Test
    void testGettersAndSetters() {
        CommentDto comment = new CommentDto();
        comment.setId(2L);
        comment.setText("Another comment");
        comment.setAuthorName("Another author");
        comment.setCreated(LocalDateTime.now());

        assertThat(comment.getId()).isEqualTo(2L);
        assertThat(comment.getText()).isEqualTo("Another comment");
        assertThat(comment.getAuthorName()).isEqualTo("Another author");
        assertThat(comment.getCreated()).isNotNull();
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        CommentDto comment1 = new CommentDto();
        comment1.setId(1L);
        comment1.setText("Text");
        comment1.setCreated(now);

        CommentDto comment2 = new CommentDto();
        comment2.setId(1L);
        comment2.setText("Text");
        comment2.setCreated(now);

        assertThat(comment1).isEqualTo(comment2);
        assertThat(comment1.hashCode()).isEqualTo(comment2.hashCode());
    }
}