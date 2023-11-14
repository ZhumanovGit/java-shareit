package ru.practicum.shareit.exception.model;

public class UserValidateException extends RuntimeException {
    public UserValidateException(String message) {
        super(message);
    }
}
