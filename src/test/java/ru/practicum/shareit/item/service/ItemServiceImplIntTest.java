package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ItemServiceImplIntTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void createItem_whenDataIsCorrect_whenReturnNewItem() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemDto createdItemDto = itemService.createItem(itemCreateDto, owner.getId());

        assertNotNull(createdItemDto);
        assertNotNull(createdItemDto.getId());
        assertEquals(itemCreateDto.getName(), createdItemDto.getName());
        assertEquals(itemCreateDto.getDescription(), createdItemDto.getDescription());
    }

    @Test
    void patchItem_whenDataIsCorrect_thenReturnUpdatedItem() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("name2")
                .description("newDesc")
                .build();

        // Act
        ItemDto patchedItemDto = itemService.patchItem(itemUpdateDto, item.getId(), owner.getId());

        // Assert
        assertNotNull(patchedItemDto);
        assertEquals(itemUpdateDto.getName(), patchedItemDto.getName());
        assertEquals(itemUpdateDto.getDescription(), patchedItemDto.getDescription());
        assertTrue(patchedItemDto.getAvailable());
    }

    @Test
    void getItemById_whenDataIsCorrect_thenReturnNeedItem() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());

        ItemDto retrievedItemDto = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(retrievedItemDto);
        assertEquals(item.getId(), retrievedItemDto.getId());
        assertEquals(item.getName(), retrievedItemDto.getName());
        assertEquals(item.getDescription(), retrievedItemDto.getDescription());
    }

    @Test
    void getItemsByOwnerId_whenDataIsCorrect_thenReturnOwnerItems() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .description("desc")
                .name("second")
                .available(true)
                .owner(owner)
                .build());

        List<ItemInfoDto> itemsByOwnerId = itemService.getItemsByOwnerId(owner.getId(), 0, 10);

        assertNotNull(itemsByOwnerId);
        assertFalse(itemsByOwnerId.isEmpty());
        assertEquals(2, itemsByOwnerId.size());
        assertEquals(item.getId(), itemsByOwnerId.get(0).getId());
        assertEquals(item.getName(), itemsByOwnerId.get(0).getName());
        assertEquals(item.getDescription(), itemsByOwnerId.get(0).getDescription());
        assertEquals(item2.getId(), itemsByOwnerId.get(1).getId());
        assertEquals(item2.getName(), itemsByOwnerId.get(1).getName());
        assertEquals(item2.getDescription(), itemsByOwnerId.get(1).getDescription());
    }

    @Test
    void getItemsByNameOrDesc_whenDataIsCorrect_thenReturnItems() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());

        List<ItemDto> itemsByNameOrDesc = itemService.getItemsByNameOrDesc("nam", 0, 10);

        assertNotNull(itemsByNameOrDesc);
        assertFalse(itemsByNameOrDesc.isEmpty());
        assertEquals(2, itemsByNameOrDesc.size());
        assertEquals(item.getId(), itemsByNameOrDesc.get(0).getId());
        assertEquals(item.getName(), itemsByNameOrDesc.get(0).getName());
        assertEquals(item.getDescription(), itemsByNameOrDesc.get(0).getDescription());
        assertEquals(item2.getId(), itemsByNameOrDesc.get(1).getId());
        assertEquals(item2.getName(), itemsByNameOrDesc.get(1).getName());
        assertEquals(item2.getDescription(), itemsByNameOrDesc.get(1).getDescription());
    }

    @Test
    void deleteItemById_whenDataIsCorrect_thenDeleteItem() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());

        itemService.deleteItemById(item.getId());

        assertFalse(itemRepository.existsById(item.getId()));
    }

    @Test
    void deleteItems_whenDataIsCorrect_thenDeleteAllItems() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        Item item = itemRepository.save(Item.builder()
                .description("desc")
                .name("name")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .description("desc")
                .name("second")
                .available(true)
                .owner(owner)
                .build());

        itemService.deleteItems();

        assertTrue(itemRepository.findAll().isEmpty());
    }
}

