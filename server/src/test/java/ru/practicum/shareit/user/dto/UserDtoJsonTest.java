package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void serializeUserDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("test@example.com");
    }

    @Test
    void deserializeInvalidEmail_shouldFailValidation() {
        UserDto userDto = new UserDto();
        userDto.setEmail("invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Некорректный формат email");
    }
}
