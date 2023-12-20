package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestMapperTest {
    ItemMapper itemMapper = new ItemMapper();
    RequestMapper mapper = new RequestMapper(itemMapper);

    @Test
    public void createToItemToItemRequestTest() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description("test")
                .build();

        ItemRequest itemRequest = mapper.createDtoToItemRequest(dto);

        assertEquals(dto.getDescription(), itemRequest.getDescription());
    }

    @Test
    public void itemRequestToInfoDtoTest() {
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("test")
                .created(dateTime)
                .build();
        Item first = Item.builder()
                .id(1L)
                .name("first")
                .description("firstDesc")
                .requestId(itemRequest.getId())
                .available(true)
                .build();
        Item second = Item.builder()
                .id(2L)
                .name("second")
                .description("secondDesc")
                .requestId(itemRequest.getId())
                .available(true)
                .build();
        Item third = Item.builder()
                .id(3L)
                .name("third")
                .description("thirdDesc")
                .requestId(itemRequest.getId())
                .available(true)
                .build();
        List<Item> items = List.of(first, second, third);

        ItemRequestInfoDto actual = mapper.itemRequestToInfoDto(itemRequest, items);

        assertEquals(1, actual.getId());
        assertEquals("test", actual.getDescription());
        assertEquals(dateTime, actual.getCreated());
        assertEquals(3, actual.getItems().size());
    }
}