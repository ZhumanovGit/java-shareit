package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}
