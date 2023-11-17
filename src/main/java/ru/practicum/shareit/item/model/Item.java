package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {
    private Long id;
    private User owner;
    private String name;
    private String description;
    private Boolean available;
}
