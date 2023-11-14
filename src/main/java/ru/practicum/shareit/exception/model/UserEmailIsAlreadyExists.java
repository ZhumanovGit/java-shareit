package ru.practicum.shareit.exception.model;

public class UserEmailIsAlreadyExists extends RuntimeException {
    public UserEmailIsAlreadyExists(String message) {
        super(message);
    }
}
