package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.StateStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.BookingException;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public CreatedBookingDto createBooking(@Valid @RequestBody BookingDto dto,
                                           @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Обработка запроса на создание нового бронирования для пользователя с id = {}", bookerId);
        CreatedBookingDto newBooking = bookingService.createBooking(dto, bookerId);
        log.info("создано новое бронирование с id = {}", newBooking.getId());
        return newBooking;
    }

    @PatchMapping("/{bookingId}")
    public CreatedBookingDto patchBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                          @PathVariable long bookingId,
                                          @RequestParam(name = "approved") boolean approved) {

        log.info("Обработка запроса на подстверждение бронирования с id = {}, пользователем с id = {}", bookingId, ownerId);
        CreatedBookingDto newStatusBooking = bookingService.approveBooking(bookingId, approved, ownerId);
        log.info("Статус бронирования обновлен на {}", newStatusBooking.getStatus());
        return newStatusBooking;
    }

    @GetMapping("/{bookingId}")
    public CreatedBookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable long bookingId) {
        log.info("Обработка запроса на получение бронирования с id = {} пользователем с id = {}", bookingId, userId);
        CreatedBookingDto booking = bookingService.getBookingById(bookingId, userId);
        log.info("Получена бронь с id = {}", booking.getId());
        return booking;
    }

    @GetMapping
    public List<CreatedBookingDto> getUserBookings(@RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        StateStatus value;
        try {
            value = StateStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: " + state);
        }
        log.info("Обработка запроса на получение всех бронирований пользователя с id = {}, параметр поиска: {}", userId, value);
        List<CreatedBookingDto> bookings = bookingService.getAllBookingsForUser(userId, value);
        log.info("Получен список длиной {}", bookings.size());
        return bookings;

    }

    @GetMapping("/owner")
    public List<CreatedBookingDto> getOwnerBookings(@RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") long ownerId) {
        StateStatus value;
        try {
            value = StateStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: " + state);
        }
        log.info("Обработка запроса на получение всех бронирований пользователя с id = {}, параметр поиска: {}", ownerId, value);
        List<CreatedBookingDto> bookings = bookingService.getAllBookingForOwner(ownerId, value);
        log.info("Получен список длиной {}", bookings.size());
        return bookings;

    }
}
