package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("select it " +
            "from Item as it " +
            "where upper(it.name) like upper(concat('%', ?1, '%')) " +
            "or upper(it.description) like upper(concat('%', ?1, '%'))")
    Page<Item> findAllByNameOrDesc(String searchString, Pageable pageable);

    void deleteAllByOwnerId(Long ownerId);

    List<Item> findAllByRequestIdIn(List<Long> ids);

    List<Item> findAllByRequestId(Long requestId);
}
