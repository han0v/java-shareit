package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void createUser_whenValidRequest_returnsOk() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("valid@email.com");
        userDto.setName("Name");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient).createUser(any());
    }

    @Test
    void updateUser_whenInvalidEmail_returnsBadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("invalid-email");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, Mockito.never()).updateUser(any(), any());
    }

    @Test
    void deleteUser_whenExists_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userClient).deleteUser(eq(1L));
    }

    @Test
    void getUserById_whenClientReturnsUser_shouldReturnOk() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        when(userClient.getUserById(any())).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllUsers_whenClientReturnsList_shouldReturnOk() throws Exception {
        when(userClient.getAllUsers()).thenReturn(List.of(new UserDto(), new UserDto()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateUser_whenEmailInvalidInDto_shouldNotCallClient() throws Exception {
        UserDto invalidDto = new UserDto();
        invalidDto.setEmail("invalid");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(any(), any());
    }
}

