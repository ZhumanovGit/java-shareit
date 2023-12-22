package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void findAllByNameOrDesc_whenDbIsFilled_thenReturnPageOfItems() {
        String searchString = "tEst";
        User user = entityManager.merge(User.builder().id(1L).name("test").email("test@com").build());
        Item first = Item.builder()
                .id(1L)
                .owner(user)
                .name("test")
                .description("testDesc")
                .available(true)
                .requestId(null)
                .build();
        Item second = Item.builder()
                .id(2L)
                .owner(user)
                .name("asd")
                .description("test")
                .available(true)
                .requestId(null)
                .build();
        Item third = Item.builder()
                .id(3L)
                .owner(user)
                .name("fjwo")
                .description("teST")
                .available(true)
                .requestId(null)
                .build();
        entityManager.merge(user);
        entityManager.merge(first);
        entityManager.merge(second);
        entityManager.merge(third);

        List<Item> items = itemRepository.findAllByNameOrDesc(searchString, PageRequest.of(0, 10));

        assertEquals(2, items.size());
    }

    @Test
    public void findAllByNameOrDesc_whenDbIsEmpty_thenReturnPageOfItems() {
        List<Item> items = itemRepository.findAllByNameOrDesc("teset", PageRequest.of(0, 10));

        assertEquals(0, items.size());
    }


}