package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime time, Sort sort);

    Optional<Booking> findByIdAndItemOwnerId(long itemId, long itemOwnerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and b.start < localtimestamp " +
            "and b.end > localtimestamp " +
            "and b.status != 'REJECTED'")
    List<Booking> findAllCurrentBookingsForItem(long itemId);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.start < localtimestamp " +
            "and b.end > localtimestamp ")
    List<Booking> findAllCurrentBookings(Long bookerId, Sort sort);

    List<Booking> findALlByBookerIdAndStartAfter(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStatusIs(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime time, Sort sort);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and b.start < localtimestamp " +
            "and b.end > localtimestamp ")
    List<Booking> findAllCurrentBookingsForOwner(long ownerId, Sort sort);


    List<Booking> findALlByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusIs(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusIs(Long itemId,
                                                                 LocalDateTime time,
                                                                 BookingStatus status,
                                                                 Sort sort);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusIsNot(Long itemId,
                                                                   LocalDateTime time,
                                                                   BookingStatus status,
                                                                   Sort sort);

    List<Booking> findAllByItemIdInAndStartBeforeAndStatusIs(List<Long> itemIds,
                                                             LocalDateTime time,
                                                             BookingStatus status);

    List<Booking> findAllByItemIdInAndStartAfterAndStatusIsNot(List<Long> itemIds,
                                                               LocalDateTime time,
                                                               BookingStatus status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndBefore(Long bookerId,
                                                               Long itemId,
                                                               LocalDateTime time,
                                                               Sort sort);
}
