package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    private final UserDto userDto = new UserDto(1L, "Name", "email@ya.ru");

    @Test
    void create() throws Exception {
        final UserCreateDto userCreateDto = new UserCreateDto("Name", "email@yandex.ru");
        when(userService.create(any())).thenReturn(userDto);
        mvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userCreateDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void update() throws Exception {
        final UserUpdateDto userUpdateDto = new UserUpdateDto(1L, "Name", "");
        when(userService.update(any())).thenReturn(userDto);
        mvc.perform(
                        patch("/users/1")
                                .content(mapper.writeValueAsString(userUpdateDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(any())).thenReturn(userDto);
        mvc.perform(
                        get("/users/1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void delete() throws Exception {
        doThrow(new NotFoundException("text")).
                doNothing().when(userService).delete(3L);
        doNothing().when(userService).delete(2L);
        mvc.perform(
                        MockMvcRequestBuilders.delete("/users/{userId}", 2L))
                .andExpect(status().isOk());
        mvc.perform(
                        MockMvcRequestBuilders.delete("/users/{userId}", 3L))
                .andExpect(status().isNotFound());

    }
}