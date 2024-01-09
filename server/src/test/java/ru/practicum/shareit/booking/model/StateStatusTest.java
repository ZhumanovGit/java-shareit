package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.BookingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateStatusTest {

    @Test
    public void getFromString_whenGivenAll_thenReturnAll() {
        String value = "All";

        StateStatus result = StateStatus.getFromString(value);

        assertEquals(StateStatus.ALL, result);
    }

    @Test
    public void getFromString_whenGivenCurrent_thenReturnAll() {
        String value = "Current";

        StateStatus result = StateStatus.getFromString(value);

        assertEquals(StateStatus.CURRENT, result);
    }

    @Test
    public void getFromString_whenGivenPast_thenReturnAll() {
        String value = "Past";

        StateStatus result = StateStatus.getFromString(value);

        assertEquals(StateStatus.PAST, result);
    }

    @Test
    public void getFromString_whenGivenFuture_thenReturnAll() {
        String value = "future";

        StateStatus result = StateStatus.getFromString(value);

        assertEquals(StateStatus.FUTURE, result);
    }

    @Test
    public void getFromString_whenGivenWaiting_thenReturnAll() {
        String value = "waiting";

        StateStatus result = StateStatus.getFromString(value);

        assertEquals(StateStatus.WAITING, result);
    }

    @Test
    public void getFromString_whenGivenRejected_thenReturnAll() {
        String value = "rejected";

        StateStatus result = StateStatus.getFromString(value);

        assertEquals(StateStatus.REJECTED, result);
    }

    @Test
    public void getFromString_whenGivenUnknown_thenThrowException() {
        String value = "unknown";
        String expectedResponse = "Unknown state: " + value.toUpperCase();

        Throwable throwable = assertThrows(BookingException.class, () -> StateStatus.getFromString(value));

        assertEquals(expectedResponse, throwable.getMessage());
    }

}