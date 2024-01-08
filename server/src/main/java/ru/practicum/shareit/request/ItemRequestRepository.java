package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>, QuerydslPredicateExecutor<ItemRequest> {

    List<ItemRequest> findAllByOwnerId(long ownerId, Sort sort);

    List<ItemRequest> findAllByOwnerIdNot(long ownerId, Pageable pageable);

    void deleteAllByOwnerId(long ownerId);
}
