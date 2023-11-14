package ru.practicum.shareit.exception.model;

public class ItemValidateException extends RuntimeException {
    public ItemValidateException(String message) {
        super(message);
    }
}
