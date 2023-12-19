package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Sql({"/ru/practicum/shareit/sql-scripts/item-storage-test.sql"})
    public void findAllByOwnerId_whenItemsWasFound_thenReturnPageOfItems() {
        long ownerId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Item> items = itemRepository.findAllByOwnerId(ownerId, pageRequest);

        assertEquals(3, items.getSize());
    }

    @Test
    public void findALlByOwnerId_whenItemsWasNotFound_thenReturnEmptyPage() {

    }
}