package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void findAllByOwnerId_whenDbIsFilled_thenReturnPageOfItems() {
        long ownerId = 1L;
        User user = User.builder().id(1L).name("test").email("test@com").build();
        Item first = Item.builder()
                .id(1L)
                .owner(user)
                .description("test")
                .available(true)
                .requestId(null)
                .build();
        Item second = Item.builder()
                .id(2L)
                .owner(user)
                .description("test")
                .available(true)
                .requestId(null)
                .build();
        Item third = Item.builder()
                .id(3L)
                .owner(user)
                .description("test")
                .available(true)
                .requestId(null)
                .build();
        entityManager.merge(user);
        entityManager.merge(first);
        entityManager.merge(second);
        entityManager.merge(third);

        Page<Item> items = itemRepository.findAllByOwnerId(ownerId, PageRequest.of(0, 10));

        assertEquals(3, items.getSize());
    }

    @Test
    public void findAllByOwnerId_whenDbIsEmpty_thenReturnEmptyPage() {
        long ownerId = 1L;
        Page<Item> items = itemRepository.findAllByOwnerId(ownerId, PageRequest.of(0, 10));

        assertEquals(0, items.getSize());
    }

    @Test
    public void findAllByOwnerId_whenDbIsFilled_thenReturnListOfItems() {
        long ownerId = 1L;
        User user = User.builder().id(1L).name("test").email("test@com").build();
        Item first = Item.builder()
                .id(1L)
                .owner(user)
                .description("test")
                .available(true)
                .requestId(null)
                .build();
        Item second = Item.builder()
                .id(2L)
                .owner(user)
                .description("test")
                .available(true)
                .requestId(null)
                .build();
        Item third = Item.builder()
                .id(3L)
                .owner(user)
                .description("test")
                .available(true)
                .requestId(null)
                .build();
        entityManager.merge(user);
        entityManager.merge(first);
        entityManager.merge(second);
        entityManager.merge(third);

        List<Item> items = itemRepository.findALlByOwnerId(ownerId);

        assertEquals(3, items.size());
    }

    @Test
    public void findAllByOwnerId_whenDbIsEmpty_thenReturnEmptyList() {
        long ownerId = 1L;
        List<Item> items = itemRepository.findALlByOwnerId(ownerId);

        assertEquals(0, items.size());
    }

    @Test
    public void findAllByNameOrDesc_whenDbIsFilled_thenReturnPageOfItems() {
        String searchString = "tEst";
        User user = User.builder().id(1L).name("test").email("test@com").build();
        Item first = Item.builder()
                .id(1L)
                .owner(user)
                .name("test")
                .available(true)
                .requestId(null)
                .build();
        Item second = Item.builder()
                .id(2L)
                .owner(user)
                .description("test")
                .available(true)
                .requestId(null)
                .build();
        Item third = Item.builder()
                .id(3L)
                .owner(user)
                .description("teST")
                .available(true)
                .requestId(null)
                .build();
        entityManager.merge(user);
        entityManager.merge(first);
        entityManager.merge(second);
        entityManager.merge(third);

        Page<Item> items = itemRepository.findAllByNameOrDesc(searchString, PageRequest.of(0, 10));

        assertEquals(3, items.getSize());
    }

    @Test
    public void findAllByNameOrDesc_whenDbIsEmpty_thenReturnPageOfItems() {
        Page<Item> items = itemRepository.findAllByNameOrDesc("teset", PageRequest.of(0, 10));

        assertEquals(3, items.getSize());
    }



}