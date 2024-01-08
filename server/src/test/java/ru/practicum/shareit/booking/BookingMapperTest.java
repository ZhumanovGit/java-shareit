package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    BookingMapper bookingMapper = new BookingMapper();

    @Test
    public void bookingCreateDtoToBookingTest() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .start(LocalDateTime.of(2024, 1, 1, 1, 1))
                .end(LocalDateTime.of(2024, 2, 1, 1, 1))
                .build();

        Booking result = bookingMapper.bookingCreateDtoToBooking(dto);

        assertEquals(dto.getStart(), result.getStart());
        assertEquals(dto.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    public void bookingToBookingDtoTest() {
        Item item = Item.builder()
                .id(1L)
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 1, 1))
                .end(LocalDateTime.of(2024, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        BookingDto dto = bookingMapper.bookingToBookingDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getItem().getId(), dto.getItem().getId());
        assertEquals(booking.getBooker().getId(), dto.getBooker().getId());
        assertEquals(booking.getStatus(), dto.getStatus());
    }

    @Test
    public void bookingToItemBookingDtoTest() {
        Item item = Item.builder()
                .id(1L)
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 1, 1))
                .end(LocalDateTime.of(2024, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        ItemBookingDto dto = bookingMapper.bookingToItemBookingDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getBooker().getId(), dto.getBookerId());
    }
}