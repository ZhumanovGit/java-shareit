package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.comment.dto.CreatedCommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class CreatedItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;
    private List<CreatedCommentDto> comments;
}
