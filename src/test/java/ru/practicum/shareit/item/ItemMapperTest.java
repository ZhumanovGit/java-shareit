package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ItemMapperTest {
    ItemMapper mapper = new ItemMapper();

    @Test
    public void itemCreateDtoToItemTest() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("test")
                .description("test desc")
                .requestId(1L)
                .available(true)
                .build();

        Item item = mapper.itemCreateDtoToItem(dto);

        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getRequestId(), item.getRequestId());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }

    @Test
    public void itemUpdateDtoToItemTest() {
        ItemUpdateDto dto = ItemUpdateDto.builder()
                .name("test")
                .description("test desc")
                .available(true)
                .build();

        Item item = mapper.itemUpdateDtoToItem(dto);

        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }

    @Test
    public void itemToItemDtoTest() {
        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test desc")
                .requestId(1L)
                .available(true)
                .build();

        ItemDto dto = mapper.itemToItemDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getRequestId(), dto.getRequestId());
        assertEquals(item.getAvailable(), dto.getAvailable());
    }

    @Test
    public void itemToItemInfoDto_whenNextAndLastBookingAreFound_whenReturnDto() {
        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test desc")
                .requestId(1L)
                .available(true)
                .build();
        User user = User.builder().id(1L).build();
        Booking next = Booking.builder().id(1L).booker(user).build();
        Booking last = Booking.builder().id(2L).booker(user).build();
        Comment first = Comment.builder().id(1L).build();
        Comment second = Comment.builder().id(2L).build();
        Comment third = Comment.builder().id(3L).build();
        List<Comment> comments = List.of(first, second, third);

        ItemInfoDto dto = mapper.itemToItemInfoDto(item, comments, next, last);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(3, dto.getComments().size());
        assertEquals(last.getId(), dto.getLastBooking().getBookerId());
        assertEquals(next.getId(), dto.getNextBooking().getId());
    }

    @Test
    public void itemToItemInfoDto_whenNextAndLastBookingWasNotFound_whenReturnDto() {
        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test desc")
                .requestId(1L)
                .available(true)
                .build();
        Comment first = Comment.builder().id(1L).build();
        Comment second = Comment.builder().id(2L).build();
        Comment third = Comment.builder().id(3L).build();
        List<Comment> comments = List.of(first, second, third);

        ItemInfoDto dto = mapper.itemToItemInfoDto(item, comments, null, null);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(3, dto.getComments().size());
    }
}