package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.StateStatus;

import java.util.List;

public interface BookingService {

    CreatedBookingDto createBooking(BookingDto dto, Long bookerId);

    CreatedBookingDto approveBooking(Long bookingId, boolean isApproved, Long ownerId);

    CreatedBookingDto getBookingById(Long bookingId, Long userId);

    List<CreatedBookingDto> getAllBookingsForUser(Long userId, StateStatus state);

    List<CreatedBookingDto> getAllBookingForOwner(Long ownerId, StateStatus state);
}
