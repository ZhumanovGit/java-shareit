package ru.practicum.shareit.exception.model;

public class ValidateException extends RuntimeException {
    public ValidateException(String message) {
        super(message);
    }
}
