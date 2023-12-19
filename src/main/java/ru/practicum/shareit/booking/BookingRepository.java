package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

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

    Optional<Booking> findFirstBy(BooleanExpression e, Sort sort);
}
