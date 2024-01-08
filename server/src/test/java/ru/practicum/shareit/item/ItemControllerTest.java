package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @MockBean
    CommentService commentService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void getAllUserItems_whenFromAndSizeNotGiven_thenReturnAllUserItems() throws Exception {
        long userId = 1L;
        List<ItemInfoDto> mockItems = List.of(
                ItemInfoDto.builder().id(1L).build(),
                ItemInfoDto.builder().id(2L).build()
        );
        when(itemService.getItemsByOwnerId(userId,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")))).thenReturn(mockItems);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(mockItems)));
    }

    @Test
    public void getAllUserItems_whenRequestIsCorrect_thenReturnUserItems() throws Exception {
        long userId = 1L;
        List<ItemInfoDto> mockItems = List.of(
                ItemInfoDto.builder().id(1L).build(),
                ItemInfoDto.builder().id(2L).build()
        );
        when(itemService.getItemsByOwnerId(userId, PageRequest.of(0, 3,
                Sort.by(Sort.Direction.ASC, "id")))).thenReturn(mockItems);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(mockItems)));
    }

    @Test
    public void getAllUserItems_whenRequestHasNoNeedHeader_thenReturnStatus500() throws Exception {

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void getItemById_whenRequestIsCorrect_thenReturnNeedItem() throws Exception {
        Long itemId = 1L;
        Long requesterId = 1L;
        ItemDto result = ItemDto.builder()
                .id(1L)
                .build();
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(result);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getItemById_whenRequestMissHeader_whenReturnStatus500() throws Exception {

        mvc.perform(get("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void searchItems_whenRequestIsCorrectWithAllParams_thenReturnListOfItems() throws Exception {
        String searchString = "TeSt";
        Long requesterId = 1L;
        ItemDto first = ItemDto.builder()
                .id(1L)
                .name("test")
                .build();
        ItemDto second = ItemDto.builder()
                .id(1L)
                .description("test")
                .build();
        List<ItemDto> result = List.of(first, second);
        when(itemService.getItemsByNameOrDesc(anyString(), any(Pageable.class))).thenReturn(result);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", requesterId)
                        .param("text", searchString)
                        .param("from", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void searchItems_whenRequestIsCorrectWithNoParams_thenReturnListOfItems() throws Exception {
        String searchString = "TeSt";
        Long requesterId = 1L;
        ItemDto first = ItemDto.builder()
                .id(1L)
                .name("test")
                .build();
        ItemDto second = ItemDto.builder()
                .id(1L)
                .description("test")
                .build();
        List<ItemDto> result = List.of(first, second);
        when(itemService.getItemsByNameOrDesc(anyString(), any(Pageable.class))).thenReturn(result);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", requesterId)
                        .param("text", searchString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void createItem_whenRequestIsCorrect_thenReturnNewItem() throws Exception {
        long ownerId = 1L;
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("name")
                .description("desc")
                .requestId(1L)
                .available(true)
                .build();
        ItemDto result = ItemDto.builder().id(1L).build();
        when(itemService.createItem(any(ItemCreateDto.class), anyLong())).thenReturn(result);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void createItem_whenRequestMissHeader_thenReturnStatus500() throws Exception {

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void patchItem_whenRequestIsCorrect_thenReturnUpdatedItem() throws Exception {
        Long itemId = 1L;
        Long ownerId = 1L;
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("newName")
                .build();
        ItemDto result = ItemDto.builder()
                .name("newName")
                .id(1L)
                .description("nice")
                .build();
        when(itemService.patchItem(any(ItemUpdateDto.class), anyLong(), anyLong())).thenReturn(result);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void patchItem_whenRequestMissHeader_thenReturnStatus500() throws Exception {
        mvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void deleteItemById_whenRequestIsCorrect_thenDeleteItem() throws Exception {
        Long itemId = 1L;

        mvc.perform(delete("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void postComment_whenRequestIsCorrect_thenReturnNewComment() throws Exception {
        Long itemId = 1L;
        Long authorId = 1L;
        CommentCreateDto createDto = CommentCreateDto.builder()
                .text("text")
                .build();
        CommentDto result = CommentDto.builder()
                .id(1L)
                .build();
        when(commentService.createNewComment(any(CommentCreateDto.class), anyLong(), anyLong())).thenReturn(result);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void postComment_whenRequestMissHeader_thenReturnStatus500() throws Exception {

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}