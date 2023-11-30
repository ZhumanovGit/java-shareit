package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    /*"select new ru.practicum.item.model.Item(it.id, it.name, it.description, it.available) " +
            "from Item as it " +
            "where upper(it.name) like upper(concat('%', ?1, '%')) " +
            "or upper(it.description) like upper(concat('%', ?1, '%'))"*/

    @Query("select it " +
            "from Item as it " +
            "where upper(it.name) like upper(concat('%', ?1, '%')) " +
            "or upper(it.description) like upper(concat('%', ?1, '%'))")
    List<Item> findAllByNameOrDesc(String searchString);

    void deleteAllByOwnerId(Long ownerId);
}
