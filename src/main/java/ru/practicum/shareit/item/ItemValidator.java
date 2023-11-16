package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.model.ValidateException;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemValidator {

    public void validateItemForCreate(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidateException("Имя объекта не может быть пустым");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidateException("Описание обекта не может быть пустым");
        }

        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new ValidateException("объект не может быть занят при создании");
        }
    }

    public void validateItemForUpdate(Item item) {
        if (item.getName() != null && item.getName().isBlank()) {
            throw new ValidateException("Имя объекта не может быть пустым");
        }

        if (item.getDescription() != null && item.getDescription().isBlank()) {
            throw new ValidateException("Описание обекта не может быть пустым");
        }
    }
}
