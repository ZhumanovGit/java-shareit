package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    public void createRequest_whenRequestIsCorrect_thenReturnNewItemRequest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestCreateDto createDto = ItemRequestCreateDto.builder()
                .description("test")
                .build();
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .created(now)
                .description("test")
                .build();
        when(service.createItemRequest(any(ItemRequestCreateDto.class), anyLong())).thenReturn(dto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));
    }

    @Test
    public void createRequest_whenHeaderWasNotFound_thenReturnStatus500() throws Exception {
        ItemRequestCreateDto createDto = ItemRequestCreateDto.builder()
                .description("test")
                .build();

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void getAllYours_whenRequestIsCorrect_thenReturnListOfRequests() throws Exception {
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

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getAllYours_whenMissHeader_thenReturnStatus500() throws Exception {
        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
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
        when(service.getAllRequests(anyLong(), any(Pageable.class))).thenReturn(result);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getAllOthers_whenMissHeader_thenReturnStatus500() throws Exception {

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void getAllOthers_whenFromOrSizeIsEmpty_thenReturnListOfRequests() throws Exception {
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
        when(service.getAllRequests(anyLong(), any(Pageable.class))).thenReturn(result);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getRequestById_whenRequestIsCorrect_thenReturnNeedRequest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestInfoDto first = ItemRequestInfoDto.builder()
                .id(1L)
                .created(now)
                .description("test")
                .build();
        when(service.getRequestById(anyLong(), anyLong())).thenReturn(first);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(first)));
    }

    @Test
    public void getRequestById_whenRequestMissHeader_thenReturnStatus500() throws Exception {

        mvc.perform(get("/requests/{requestId}", 1L))
                .andExpect(status().is5xxServerError());
    }
}