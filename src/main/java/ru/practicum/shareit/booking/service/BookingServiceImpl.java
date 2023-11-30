package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public CreatedBookingDto createBooking(BookingDto dto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + bookerId + " не найден"));
        Long itemId = dto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Объект с id = " + itemId + " не найден"));
        if (!item.getAvailable()) {
            throw new BookingException("Не найдена свободная вещь с таким id");
        }
        long ownerId = item.getOwner().getId();
        if (ownerId == bookerId) {
            throw new NotFoundException("id владельца и арендатора совпадают");
        }
        Booking booking = mapper.bookingDtoToBooking(dto);
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new BookingException("Некорректное время аренды");
        }
        booking.setBooker(booker);
        booking.setItem(item);
        Booking createdBooking = bookingRepository.save(booking);
        return mapper.bookingToCreatedBookingDto(createdBooking);
    }

    @Override
    @Transactional
    public CreatedBookingDto approveBooking(Long bookingId, boolean isApproved, Long ownerId) {
        User booker = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        long bookingOwnerId = booking.getItem().getOwner().getId();
        if (bookingOwnerId != ownerId) {
            throw new NotFoundException("Не верный владелец вещи");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingException("Бронирование уже подстверждено");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return mapper.bookingToCreatedBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public CreatedBookingDto getBookingById(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        long bookingOwnerId = booking.getItem().getOwner().getId();
        long bookingBookerId = booking.getBooker().getId();
        if (bookingOwnerId != userId && bookingBookerId != userId) {
            throw new NotFoundException("Пользователь не связан с данным бронированием");
        }
        return mapper.bookingToCreatedBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreatedBookingDto> getAllBookingsForUser(Long userId, StateStatus state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                result = bookingRepository.findAllCurrentBookings(userId);
                break;
            case FUTURE:
                result = bookingRepository.findALlByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusIs(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusIs(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new NotFoundException("Не найден параметр поиска");
        }
        return result.stream()
                .map(mapper::bookingToCreatedBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreatedBookingDto> getAllBookingForOwner(Long ownerId, StateStatus state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        if (items.isEmpty()) {
            throw new NotFoundException("Не найдены вещи для данного владельца");
        }
        List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case CURRENT:
                result = bookingRepository.findAllCurrentBookingsForOwner(ownerId);
                break;
            case FUTURE:
                result = bookingRepository.findALlByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new NotFoundException("Не найден параметр поиска");
        }
        return result.stream()
                .map(mapper::bookingToCreatedBookingDto)
                .collect(Collectors.toList());
    }
}
