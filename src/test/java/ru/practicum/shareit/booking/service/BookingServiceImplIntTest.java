package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class BookingServiceImplIntTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void createBooking_whenDataIsCorrect_thenReturnNewBooking() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        User booker = userRepository.save(User.builder()
                .name("name")
                .email("booker@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto createdBookingDto = bookingService.createBooking(bookingCreateDto, booker.getId(), true);

        assertNotNull(createdBookingDto);
        assertNotNull(createdBookingDto.getId());
        assertEquals(bookingCreateDto.getItemId(), createdBookingDto.getItem().getId());
        assertEquals(bookingCreateDto.getStart(), createdBookingDto.getStart());
        assertEquals(bookingCreateDto.getEnd(), createdBookingDto.getEnd());
        assertEquals(booker.getId(), createdBookingDto.getBooker().getId());
        assertEquals(item.getId(), createdBookingDto.getItem().getId());
    }

    @Test
    void approveBooking_whenDataIsCorrect_thenReturnBooking() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        User booker = userRepository.save(User.builder()
                .name("name")
                .email("booker@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .item(item)
                .build());

        BookingDto approvedBookingDto = bookingService.approveBooking(booking.getId(), true, owner.getId());

        assertNotNull(approvedBookingDto);
        assertEquals(BookingStatus.APPROVED, approvedBookingDto.getStatus());
    }

    @Test
    void getBookingById_whenDataIsCorrect_thenReturnNeedBooking() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        User booker = userRepository.save(User.builder()
                .name("name")
                .email("booker@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .item(item)
                .build());

        BookingDto retrievedBookingDto = bookingService.getBookingById(booking.getId(), booker.getId());

        assertNotNull(retrievedBookingDto);
        assertEquals(booking.getId(), retrievedBookingDto.getId());
        assertEquals(booker.getId(), retrievedBookingDto.getBooker().getId());
    }

    @Test
    void getAllBookingsForUser_whenDataIsCorrect_thenReturnListOfBookings() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        User booker = userRepository.save(User.builder()
                .name("name")
                .email("booker@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .description("desc2")
                .name("name2")
                .available(true)
                .owner(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .item(item)
                .build());
        Booking booking2 = bookingRepository.save(Booking.builder()
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .booker(booker)
                .item(item2)
                .build());

        List<BookingDto> bookingsForUser = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.ALL, PageRequest.of(0, 10));

        assertNotNull(bookingsForUser);
        assertFalse(bookingsForUser.isEmpty());
        assertEquals(2, bookingsForUser.size());
        assertEquals(booking.getId(), bookingsForUser.get(0).getId());
        assertEquals(booking2.getId(), bookingsForUser.get(1).getId());
    }

    @Test
    void getAllBookingsForOwner_whenDataIsCorrect_thenReturnListOfBookings() {
        User owner1 = userRepository.save(User.builder()
                .name("name1")
                .email("nice@email.com")
                .build());
        User owner2 = userRepository.save(User.builder()
                .name("name")
                .email("nice2@email.com")
                .build());
        User booker = userRepository.save(User.builder()
                .name("name")
                .email("booker@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner2)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .description("desc2")
                .name("name2")
                .available(true)
                .owner(owner1)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .item(item)
                .build());
        Booking booking2 = bookingRepository.save(Booking.builder()
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .booker(booker)
                .item(item2)
                .build());

        List<BookingDto> bookingsForOwner = bookingService.getAllBookingsForOwner(owner1.getId(), StateStatus.ALL, PageRequest.of(0, 10));

        assertNotNull(bookingsForOwner);
        assertFalse(bookingsForOwner.isEmpty());
        assertEquals(1, bookingsForOwner.size());
        assertEquals(booking2.getId(), bookingsForOwner.get(0).getId());
    }
}

