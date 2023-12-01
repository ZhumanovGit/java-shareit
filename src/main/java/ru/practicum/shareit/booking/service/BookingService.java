package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StateStatus;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingCreateDto dto, Long bookerId);

    BookingDto approveBooking(Long bookingId, boolean isApproved, Long ownerId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsForUser(Long userId, StateStatus state);

    List<BookingDto> getAllBookingForOwner(Long ownerId, StateStatus state);
}
