package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.StateStatus;
import ru.practicum.shareit.exception.model.BookingException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public BookingDto createBooking(BookingCreateDto dto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + bookerId + " не найден"));
        Long itemId = dto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Объект с id = " + itemId + " не найден"));
        if (!item.getAvailable()) {
            throw new BookingException("Не найдена свободная вещь с таким id");
        }
        List<Booking> currentItemBookings = bookingRepository.checkItemBookings(itemId, dto.getStart(), dto.getEnd());
        if (!currentItemBookings.isEmpty()) {
            throw new BookingException("Данная вещь сейчас находится в аренде");
        }
        long ownerId = item.getOwner().getId();
        if (ownerId == bookerId) {
            throw new NotFoundException("id владельца и арендатора совпадают");
        }
        Booking booking = mapper.bookingCreateDtoToBooking(dto);

        booking.setBooker(booker);
        booking.setItem(item);
        Booking createdBooking = bookingRepository.save(booking);
        return mapper.bookingToBookingDto(createdBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, boolean isApproved, Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingException("Бронирование уже подтверждено");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return mapper.bookingToBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        long bookingOwnerId = booking.getItem().getOwner().getId();
        long bookingBookerId = booking.getBooker().getId();
        if (bookingOwnerId != userId && bookingBookerId != userId) {
            throw new NotFoundException("Пользователь не связан с данным бронированием");
        }
        return mapper.bookingToBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsForUser(Long userId, StateStatus state, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(userId);
        BooleanExpression queryExpression = byBookerId.and(getBookingExpression(state));
        Page<Booking> result = bookingRepository.findAll(queryExpression, pageable);

        return result.stream()
                .map(mapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsForOwner(Long ownerId, StateStatus state, Pageable pageable) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
        List<Item> items = itemRepository.findALlByOwnerId(ownerId);
        if (items.isEmpty()) {
            throw new NotFoundException("Не найдены вещи для данного владельца");
        }

        BooleanExpression byItemOwnerId = QBooking.booking.item.owner.id.eq(ownerId);
        BooleanExpression queryExpression = byItemOwnerId.and(getBookingExpression(state));
        Page<Booking> result = bookingRepository.findAll(queryExpression, pageable);

        return result.stream()
                .map(mapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    private BooleanExpression getBookingExpression(StateStatus state) {
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression expression;
        switch (state) {
            case ALL:
                expression = QBooking.booking.isNotNull();
                break;
            case PAST:
                expression = QBooking.booking.end.before(now);
                break;
            case CURRENT:
                expression = QBooking.booking.start.before(now)
                        .and(QBooking.booking.end.after(now));
                break;
            case FUTURE:
                expression = QBooking.booking.start.after(now);
                break;
            case WAITING:
                expression = QBooking.booking.status.eq(BookingStatus.WAITING);
                break;
            case REJECTED:
                expression = QBooking.booking.status.eq(BookingStatus.REJECTED);
                break;
            default:
                expression = null;
        }

        return expression;
    }
}
