package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    Optional<Booking> findByIdAndItemOwnerId(long itemId, long itemOwnerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and b.start < localtimestamp " +
            "and b.end > localtimestamp " +
            "and b.status != 'REJECTED'")
    List<Booking> findAllCurrentBookingsForItem(long itemId);

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
