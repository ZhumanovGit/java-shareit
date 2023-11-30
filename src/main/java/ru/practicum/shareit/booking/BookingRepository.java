package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.start < localtimestamp " +
            "and b.end > localtimestamp " +
            "order by b.start desc ")
    List<Booking> findAllCurrentBookings(Long bookerId);

    List<Booking> findALlByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStatusIs(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and b.start < localtimestamp " +
            "and b.end > localtimestamp " +
            "order by b.start desc ")
    List<Booking> findAllCurrentBookingsForOwner(long ownerId);

    List<Booking> findALlByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime time);

    List<Booking> findAllByItemOwnerIdAndStatusIs(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusIsOrderByStartDesc(Long itemId,
                                                                                 LocalDateTime time,
                                                                                 BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusIsNotOrderByStartAsc(Long itemId,
                                                                                  LocalDateTime time,
                                                                                  BookingStatus status);

    List<Booking> findAllByItemIdInAndStartBeforeAndStatusIs(List<Long> itemIds,
                                                             LocalDateTime time,
                                                             BookingStatus status);

    List<Booking> findAllByItemIdInAndStartAfterAndStatusIsNot(List<Long> itemIds,
                                                            LocalDateTime time,
                                                            BookingStatus status);
    Optional<Booking> findFirstByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(Long bookerId,
                                                                          Long itemId,
                                                                          LocalDateTime time);
}
