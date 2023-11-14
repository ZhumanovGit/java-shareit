package ru.practicum.shareit.user;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class User {
    private Long id;
    private String name;
    private String email;
}
