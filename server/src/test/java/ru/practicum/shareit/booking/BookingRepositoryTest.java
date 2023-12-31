package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void checkItemBookings_whenDbIsFilled_thenReturnListOfBookings() {
        User owner = entityManager.merge(User.builder().id(1L).name("test").email("test@com").build());
        User booker = entityManager.merge(User.builder().id(2L).name("test").email("test2@com").build());
        Item item = entityManager.merge(Item.builder()
                .id(1L)
                .owner(owner)
                .name("test")
                .description("testDesc")
                .available(true)
                .requestId(null)
                .build());
        Booking first = entityManager.merge(Booking.builder()
                .booker(booker)
                .item(item)
                .end(LocalDateTime.of(2025, 1, 1, 1, 1))
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.checkItemBookings(item.getId(), first.getStart(), first.getEnd());

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllCurrentBookingsForItem_whenDbIsEmpty_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.checkItemBookings(1L, LocalDateTime.now(), LocalDateTime.now());

        assertEquals(0, bookings.size());
    }

}