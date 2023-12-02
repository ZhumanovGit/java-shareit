package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public ItemDto itemToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(new ArrayList<>())
                .build();
    }

    public Item itemCreateDtoToItem(ItemCreateDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public Item itemUpdateDtoToItem(ItemUpdateDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public ItemInfoDto itemToItemInfoDto(Item item, List<Comment> comments, Booking next, Booking last) {
        ItemBookingDto nextDto = null;
        ItemBookingDto lastDto = null;

        if (next != null) {
            nextDto = ItemBookingDto.builder()
                    .id(next.getId())
                    .bookerId(next.getBooker().getId())
                    .build();
        }

        if (last != null) {
            lastDto = ItemBookingDto.builder()
                    .id(last.getId())
                    .bookerId(last.getBooker().getId())
                    .build();
        }


        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .lastBooking(lastDto)
                .nextBooking(nextDto)
                .build();


    }
}
