package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    BookingMapper mapper;
    BookingServiceImpl bookingService;

    @BeforeEach
    public void beforeEach() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        mapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, mapper);
    }

    void assertEqualBooking(BookingDto o1, BookingDto o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getStart(), o2.getStart());
        assertEquals(o1.getEnd(), o2.getEnd());
        assertEquals(o1.getItem().getId(), o2.getItem().getId());
        assertEquals(o1.getBooker().getId(), o2.getBooker().getId());
    }

    @Test
    public void createBooking_whenDataIsCorrect_thenReturnNewBooking() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .build();
        Boolean isDataCorrect = dto.getStart().isBefore(dto.getEnd());
        Booking expectedBooking = mapper.bookingCreateDtoToBooking(dto);
        expectedBooking.setId(1L);
        expectedBooking.setItem(item);
        expectedBooking.setBooker(booker);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(Booking.builder()
                .id(1L)
                .start(dto.getStart())
                .end((dto.getEnd()))
                .item(item)
                .booker(booker)
                .build());

        BookingDto actualBooking = bookingService.createBooking(dto, booker.getId());

        assertEqualBooking(mapper.bookingToBookingDto(expectedBooking), actualBooking);
    }

    @Test
    public void createBooking_whenBookerIsNotFound_thenThrowException() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(3L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .build();
        String expectedResponse = "Пользователь с id = 2 не найден";
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.createBooking(dto, 2L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createBooking_whenItemIsNotFound_thenThrowException() {
        User booker = User.builder().id(1L).build();
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(3L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .build();
        String expectedResponse = "Объект с id = 3 не найден";
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(dto.getItemId())).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.createBooking(dto, booker.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createBooking_whenItemIsNotAvailable_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(false).owner(owner).build();
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .build();
        String expectedResponse = "Не найдена свободная вещь с таким id";
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Throwable throwable = assertThrows(BookingException.class, () -> bookingService.createBooking(dto, booker.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createBooking_whenItemHasCurrentBookings_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(1L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .build();
        String expectedResponse = "Данная вещь сейчас находится в аренде";
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllCurrentBookingsForItem(anyLong())).thenReturn(List.of(Booking.builder()
                .id(3L)
                .build()));

        Throwable throwable = assertThrows(BookingException.class, () -> bookingService.createBooking(dto, booker.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createBooking_whenBookerAndOwnerIdsSame_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(1L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .build();
        String expectedResponse = "id владельца и арендатора совпадают";
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.createBooking(dto, booker.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void approveBooking_whenDataIsCorrect_thenReturnBooking() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        Booking expectedBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByIdAndItemOwnerId(booking.getId(), owner.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(expectedBooking);

        BookingDto newBooking = bookingService.approveBooking(booking.getId(), true, owner.getId());

        assertEqualBooking(mapper.bookingToBookingDto(expectedBooking), newBooking);
    }

    @Test
    public void approveBooking_whenOwnerNotFound_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        String expectedResponse = "Пользователь с id = " + owner.getId() + " не найден";
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.approveBooking(booking.getId(), true, owner.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void approveBooking_whenBookingNotFound_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        String expectedResponse = "Бронирование с id = " + booking.getId() + " не найдено";
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.approveBooking(booking.getId(), true, owner.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void approveBooking_whenWasNotCorrectOwner_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        String expectedResponse = "Бронирование с id = " + booking.getId() + " не найдено";
        when(userRepository.findById(3L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.approveBooking(booking.getId(), true, 3L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void approveBooking_whenBookingAlreadyApproved_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        String expectedResponse = "Бронирование уже подтверждено";
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByIdAndItemOwnerId(booking.getId(), owner.getId())).thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(BookingException.class, () -> bookingService.approveBooking(booking.getId(), true, owner.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getBookingById_whenDataIsCorrect_thenReturnBooking() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto actualBooking = bookingService.getBookingById(booking.getId(), booker.getId());

        assertEqualBooking(mapper.bookingToBookingDto(booking), actualBooking);
    }

    @Test
    public void getBookingById_whenBookingWasNotFound_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        String expectedResponse = "Бронирование с id = 1 не найдено";
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), booker.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getBookingById_whenUserIsNotFound_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        String expectedResponse = "Пользователь с id = " + booker.getId() + " не найден";
        when(userRepository.findById(booker.getId())).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), booker.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getBookingById_whenUserIsNotCorrect_thenThrowException() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2021, 1, 1, 1, 1))
                .end(LocalDateTime.of(2021, 2, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        String expectedResponse = "Пользователь не связан с данным бронированием";
        when(userRepository.findById(5L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), 5L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getAllBookingsForUser_whenUserWasNotFound_thenThrowException() {
        String expectedString = "Пользователь с id = 1 не найден";
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsForUser(1L, StateStatus.ALL, PageRequest.of(0, 10)));

        assertEquals(expectedString, throwable.getMessage());
    }

    @Test
    public void getAllBookingsForUser_whenBookingsWasNotFound_thenReturnEmptyList() {
        User booker = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(Page.empty());

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.ALL, PageRequest.of(0, 10));

        assertEquals(0, result.size());
    }

    @Test
    public void getAllBookingsForUser_whenBookingsWasFoundForALL_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.ALL, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForUser_whenBookingsWasFoundForCurrent_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.CURRENT, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForUser_whenBookingsWasFoundForPast_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.PAST, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForUser_whenBookingsWasFoundForFuture_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.FUTURE, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForUser_whenBookingsWasFoundForWaiting_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.WAITING, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForUser_whenBookingsWasFoundForRejected_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.REJECTED, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForOwner_whenUserWasNotFound_thenThrowException() {
        String expectedString = "Пользователь с id = 1 не найден";
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsForOwner(1L, StateStatus.ALL, PageRequest.of(0, 10)));

        assertEquals(expectedString, throwable.getMessage());
    }

    @Test
    public void getAllBookingsForOwner_whenItemsForOwnerWasNotFound_thenThrowException() {
        String excpectedString = "Не найдены вещи для данного владельца";
        User booker = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findALlByOwnerId(anyLong())).thenReturn(Collections.emptyList());

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsForOwner(booker.getId(), StateStatus.ALL, PageRequest.of(0, 10)));

        assertEquals(excpectedString, throwable.getMessage());
    }

    @Test
    public void getAllBookingsForOwner_whenBookingsWasNotFound_thenReturnEmptyList() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findALlByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(Page.empty());

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.ALL, PageRequest.of(0, 10));

        assertEquals(0, result.size());
    }

    @Test
    public void getAllBookingsForOwner_whenBookingsWasFoundForALL_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findALlByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.ALL, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForOwner_whenBookingsWasFoundForCurrent_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findALlByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.CURRENT, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForOwner_whenBookingsWasFoundForPast_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findALlByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.PAST, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForOwner_whenBookingsWasFoundForFuture_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findALlByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.FUTURE, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForOwner_whenBookingsWasFoundForWaiting_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findALlByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.WAITING, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingsForOwner_whenBookingsWasFoundForRejected_thenReturnListOfBookings() {
        User booker = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Booking first = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking second = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2022, 1, 2, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findALlByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(first, second)));

        List<BookingDto> result = bookingService.getAllBookingsForUser(booker.getId(), StateStatus.REJECTED, PageRequest.of(0, 10));

        assertEquals(2, result.size());
    }


}