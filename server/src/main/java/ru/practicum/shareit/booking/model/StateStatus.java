package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.model.BookingException;

public enum StateStatus {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static StateStatus getFromString(String value) {
        String upperValue = value.toUpperCase();
        switch (upperValue) {
            case "ALL":
                return StateStatus.ALL;
            case "CURRENT":
                return StateStatus.CURRENT;
            case "PAST":
                return StateStatus.PAST;
            case "FUTURE":
                return StateStatus.FUTURE;
            case "WAITING":
                return StateStatus.WAITING;
            case "REJECTED":
                return StateStatus.REJECTED;
            default:
                throw new BookingException("Unknown state: " + upperValue);
        }
    }
}
