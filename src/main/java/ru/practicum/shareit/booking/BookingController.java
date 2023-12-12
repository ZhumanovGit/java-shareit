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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StateStatus;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingCreateDto dto,
                                    @RequestHeader("X-Sharer-User-Id") long bookerId) {

        Boolean isDataCorrected = dto.getStart().isBefore(dto.getEnd());
        log.info("Обработка запроса на создание нового бронирования для пользователя с id = {}", bookerId);
        BookingDto newBooking = bookingService.createBooking(dto, bookerId, isDataCorrected);
        log.info("создано новое бронирование с id = {}", newBooking.getId());
        return newBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                   @PathVariable long bookingId,
                                   @RequestParam(name = "approved") boolean approved) {

        log.info("Обработка запроса на подстверждение бронирования с id = {}, пользователем с id = {}", bookingId, ownerId);
        BookingDto newStatusBooking = bookingService.approveBooking(bookingId, approved, ownerId);
        log.info("Статус бронирования обновлен на {}", newStatusBooking.getStatus());
        return newStatusBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId) {
        log.info("Обработка запроса на получение бронирования с id = {} пользователем с id = {}", bookingId, userId);
        BookingDto booking = bookingService.getBookingById(bookingId, userId);
        log.info("Получена бронь с id = {}", booking.getId());
        return booking;
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                            @RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "1") int size) {
        StateStatus value = StateStatus.getFromString(state);
        log.info("Обработка запроса на получение всех бронирований пользователя с id = {}, параметр поиска: {}", userId, value);
        List<BookingDto> bookings = bookingService.getAllBookingsForUser(userId, value);
        log.info("Получен список длиной {}", bookings.size());
        return bookings;

    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "1") int size) {
        StateStatus value = StateStatus.getFromString(state);
        log.info("Обработка запроса на получение всех бронирований пользователя с id = {}, параметр поиска: {}", ownerId, value);
        List<BookingDto> bookings = bookingService.getAllBookingForOwner(ownerId, value);
        log.info("Получен список длиной {}", bookings.size());
        return bookings;

    }
}
