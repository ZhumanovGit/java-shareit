package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService service;
    @Autowired
    private MockMvc mvc;

    @Test
    public void getUsersTest() throws Exception {
        UserDto first = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testEmail@email1.com")
                .build();
        UserDto second = UserDto.builder()
                .id(2L)
                .name("test")
                .email("testEmail@email2.com")
                .build();
        UserDto third = UserDto.builder()
                .id(3L)
                .name("test")
                .email("testEmail@email3.com")
                .build();
        List<UserDto> result = List.of(first, second, third);
        when(service.getUsers()).thenReturn(result);

        mvc.perform((get("/users"))
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserTest() throws Exception {
        UserDto first = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testEmail@email1.com")
                .build();
        when(service.getUserById(first.getId())).thenReturn(first);

        mvc.perform((get("/users/1"))
                        .content(mapper.writeValueAsString(first))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(first.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(first.getName()), String.class))
                .andExpect(jsonPath("$.email", is(first.getEmail()), String.class));
    }

    @Test
    public void createUserTest() throws Exception {
        UserDto first = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testEmail@email1.com")
                .build();
        when(service.createUser(any(UserCreateDto.class))).thenReturn(first);

        mvc.perform((post("/users"))
                        .content(mapper.writeValueAsString(first))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(first.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(first.getName()), String.class))
                .andExpect(jsonPath("$.email", is(first.getEmail()), String.class));
    }

    @Test
    public void patchUserTest() throws Exception {
        UserDto first = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testEmail@email1.com")
                .build();
        when(service.patchUser(first.getId(), any(UserUpdateDto.class))).thenReturn(first);

        mvc.perform((post("/users/1"))
                        .content(mapper.writeValueAsString(first))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(first.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(first.getName()), String.class))
                .andExpect(jsonPath("$.email", is(first.getEmail()), String.class));
    }

    @Test
    public void deleteUserByIdTest() throws Exception {
        mvc.perform((delete("/users/1")))
                .andExpect(status().isOk());
        verify(service, times(1)).deleteUserById(any());
    }

    @Test
    public void deleteAllUserTest() throws Exception {
        mvc.perform((delete("/users")))
                .andExpect(status().isOk());
        verify(service, times(1)).deleteUsers();
    }
}