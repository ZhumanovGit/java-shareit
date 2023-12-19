package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService service;
    @Autowired
    private MockMvc mvc;

    @Test
    public void createRequestTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .created(now)
                .description("test")
                .build();
        when(service.createItemRequest(any(), any())).thenReturn(dto);

        mvc.perform((post("/requests"))
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.created", is(dto.getCreated()), LocalDateTime.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription()), String.class));
    }

    @Test
    public void getAllYours() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestInfoDto first = ItemRequestInfoDto.builder()
                .id(1L)
                .created(now)
                .description("test")
                .build();
        ItemRequestInfoDto second = ItemRequestInfoDto.builder()
                .id(2L)
                .created(now)
                .description("test")
                .build();
        ItemRequestInfoDto third = ItemRequestInfoDto.builder()
                .id(3L)
                .created(now)
                .description("test")
                .build();
        List<ItemRequestInfoDto> result = List.of(first, second, third);
        when(service.getUserRequests(anyLong())).thenReturn(result);

        mvc.perform((get("/requests"))
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllOthersTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestInfoDto first = ItemRequestInfoDto.builder()
                .id(1L)
                .created(now)
                .description("test")
                .build();
        ItemRequestInfoDto second = ItemRequestInfoDto.builder()
                .id(2L)
                .created(now)
                .description("test")
                .build();
        ItemRequestInfoDto third = ItemRequestInfoDto.builder()
                .id(3L)
                .created(now)
                .description("test")
                .build();
        List<ItemRequestInfoDto> result = List.of(first, second, third);
        when(service.getAllRequests(anyLong(), any(), any())).thenReturn(result);

        mvc.perform((get("/requests"))
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getRequestByIdTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestInfoDto first = ItemRequestInfoDto.builder()
                .id(1L)
                .created(now)
                .description("test")
                .build();
        when(service.getRequestById(anyLong(), anyLong())).thenReturn(first);

        mvc.perform((get("/requests/1"))
                        .content(mapper.writeValueAsString(first))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(first.getId()), Long.class))
                .andExpect(jsonPath("$.created", is(first.getCreated()), LocalDateTime.class))
                .andExpect(jsonPath("$.description", is(first.getDescription()), String.class));
    }
}