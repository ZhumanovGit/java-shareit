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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    public void getUsers_whenRequestIsCorrect_thenReturnListOfUsers() throws Exception {
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

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getUser_whenRequestIsCorrect_whenReturnNeedUser() throws Exception {
        UserDto first = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testEmail@email1.com")
                .build();
        when(service.getUserById(first.getId())).thenReturn(first);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(first)));
    }

    @Test
    public void createUser_whenRequestIsCorrect_thenReturnNewUser() throws Exception {
        UserCreateDto createDto = UserCreateDto.builder()
                .name("name")
                .email("niceEmail@com")
                .build();
        UserDto first = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testEmail@email1.com")
                .build();
        when(service.createUser(any(UserCreateDto.class))).thenReturn(first);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(first)));
    }

    @Test
    public void createUser_whenRequestHasWrongBody_thenReturnStatus400() throws Exception {
        UserCreateDto createDto = UserCreateDto.builder()
                .email("niceEmail@com")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patchUser_whenRequestHasCorrectBody_thenReturnUpdatedUser() throws Exception {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("test")
                .build();
        UserDto first = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testEmail@email1.com")
                .build();
        when(service.patchUser(anyLong(), any(UserUpdateDto.class))).thenReturn(first);

        mvc.perform((patch("/users/{userId}", 1L))
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(first)));
    }

    @Test
    public void patchUser_whenRequestBodyIsNotCorrect_thenReturnStatus400() throws Exception {
        UserUpdateDto dto = UserUpdateDto.builder()
                .email("wrong")
                .build();

        mvc.perform((patch("/users/{userId}", 1L))
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUserById_whenRequestIsCorrect_thenDeleteUser() throws Exception {

        mvc.perform((delete("/users/{userId}", 1L)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteAllUser_whenRequestIsCorrect_thenDeleteAllUsers() throws Exception {
        mvc.perform((delete("/users")))
                .andExpect(status().isOk());
    }
}