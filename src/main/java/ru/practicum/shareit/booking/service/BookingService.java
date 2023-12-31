package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StateStatus;

import javax.validation.constraints.AssertTrue;
import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingCreateDto dto, Long bookerId, @AssertTrue Boolean isDataCorrected);

    BookingDto approveBooking(Long bookingId, boolean isApproved, Long ownerId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsForUser(Long userId, StateStatus state, Pageable pageable);

    List<BookingDto> getAllBookingsForOwner(Long ownerId, StateStatus state, Pageable pageable);
}
