package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.ItemValidateException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.ItemValidator;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemServiceImpl itemService;

    @BeforeEach
    public void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, new ItemValidator());
    }

    void assertEqualItem(Item o1, Item o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getDescription(), o2.getDescription());
        assertEquals(o1.getAvailable(), o2.getAvailable());
        assertEquals(o1.getOwner().getId(), o2.getOwner().getId());
    }

    @Test
    public void createItem_whenItemIsValid_thenReturnNewItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("testDesc")
                .available(true)
                .build();
        Item expectedItem = Item.builder()
                .id(1L)
                .name("test")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        when(userRepository.getUserById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.createItem(item)).thenReturn(expectedItem);

        Item actualItem = itemService.createItem(item, owner.getId());

        assertEqualItem(expectedItem, actualItem);
    }

    @Test
    public void createItem_whenItemIsntValid_thenThrowValidateException() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .description("testDesc")
                .available(true)
                .build();
        String expectedResponse = "Имя объекта не может быть пустым";

        Throwable throwable = assertThrows(ItemValidateException.class, () -> itemService.createItem(item, owner.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createItem_whenOwnerIsNotFound_thenThrowUserNotFoundException() {
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .build();
        String expectedResponse = "Пользователь с id = 5 не найден";
        when(userRepository.getUserById(5)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(UserNotFoundException.class, () -> itemService.createItem(item, 5));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createItem_whenOwnerIdIsNotCorrect_thenThrowItemValidateException() {
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .build();
        String expectedResponse = "Id владельца не может быть отрицательным";

        Throwable throwable = assertThrows(ItemValidateException.class, () -> itemService.createItem(item, -5));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void patchItem_whenItemUpdatesAreCorrect_thenReturnUpdatedItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Item itemUpdates = Item.builder().id(1L).name("newTest").description("new desc").available(false).build();
        when(itemRepository.getItemById(1L)).thenReturn(Optional.of(item));

        Item updatedItem = itemService.patchItem(itemUpdates, item.getId(), owner.getId());

        assertEquals(item.getId(), updatedItem.getId());
        assertEquals(itemUpdates.getName(), updatedItem.getName());
        assertEquals(itemUpdates.getDescription(), updatedItem.getDescription());
        assertEquals(itemUpdates.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    public void patchItem_whenItemUpdatesNotCorrect_thenThrowValidateException() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Item itemUpdates = Item.builder().id(1L).name("").description("new desc").available(false).build();
        String expectedResponse = "Имя объекта не может быть пустым";

        Throwable throwable = assertThrows(ItemValidateException.class, () -> itemService.patchItem(itemUpdates, item.getId(), owner.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void patchItem_whenItemWasNotFound_thenThrowNotFoundException() {
        User owner = User.builder().id(1L).build();

        Item itemUpdates = Item.builder().id(2L).name("asdasd").description("new desc").available(false).build();
        when(itemRepository.getItemById(itemUpdates.getId())).thenReturn(Optional.empty());
        String expectedResponse = "Объект с id = " + itemUpdates.getId() + " не найден";

        Throwable throwable = assertThrows(ItemNotFoundException.class, () -> itemService.patchItem(itemUpdates, itemUpdates.getId(), owner.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void patchItem_whenOwnerIdNotCorrect_thenThrowValidateException() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Item itemUpdates = Item.builder().id(1L).name("asdasd").description("new desc").available(false).build();
        when(itemRepository.getItemById(1L)).thenReturn(Optional.of(item));
        String expectedResponse = "Id владельца не может быть отрицательным";

        Throwable throwable = assertThrows(ItemValidateException.class, () -> itemService.patchItem(itemUpdates, itemUpdates.getId(), -5L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void patchItem_whenWasAnotherOwnerId_thenThrowItemNotFoundException() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Item itemUpdates = Item.builder().id(1L).name("asdasd").description("new desc").available(false).build();
        when(itemRepository.getItemById(1L)).thenReturn(Optional.of(item));
        String expectedResponse = "Не найден данный объект у данного пользователя";

        Throwable throwable = assertThrows(ItemNotFoundException.class, () -> itemService.patchItem(itemUpdates, itemUpdates.getId(), 35));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemById_whenItemWasFound_returnItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(item.getId())).thenReturn(Optional.of(item));

        Item actualItem = itemService.getItemById(item.getId());

        assertEqualItem(item, actualItem);
    }

    @Test
    public void getItemById_whenIdNotCorrect_thenThrowValidateException() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        String expectedResponse = "id объекта не может быть отрицательным";

        Throwable throwable = assertThrows(ItemValidateException.class, () -> itemService.getItemById(-5L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemById_whenItemNotFound_thenReturnItemNotFoundException() {
        String expectedResponse = "объект с id = 3 не найден";

        Throwable throwable = assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(3L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemsByOwnerId_whenOwnerIdNotCorrect_thenThrowItemValidateException() {
        String expectedResponse = "id владельца не может быть отрицательным";

        Throwable throwable = assertThrows(ItemValidateException.class, () -> itemService.getItemsByOwnerId(-1L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemsByOwnerId_whenOwnerWasNotFound_thenThrowNotFoundException() {
        String expectedResponse = "Пользователь с id = 5 не найден";
        when(userRepository.getUserById(5)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(UserNotFoundException.class, () -> itemService.getItemsByOwnerId(5L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemsByOwnerId_whenOwnerIdCorrect_thenReturnListOfItems() {
        User owner = User.builder().id(1L).build();
        Item item1 = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Item item3 = Item.builder()
                .id(3L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        when(userRepository.getUserById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.getItemsByOwnerId(owner.getId())).thenReturn(List.of(item1, item2, item3));

        List<Item> items = itemService.getItemsByOwnerId(owner.getId());

        assertEquals(3, items.size());
    }

    @Test
    public void getItemsByNameOrDesc_whenStringIsBlank_thenReturnEmptyList() {
        List<Item> items = itemService.getItemsByNameOrDesc("");

        assertEquals(0, items.size());
    }

    @Test
    public void getItemsByNameOrDesc_whenStringIsCorrect_thenReturnListOfItems() {
        User owner = User.builder().id(1L).build();
        Item item1 = Item.builder()
                .id(1L)
                .name("test")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Item item3 = Item.builder()
                .id(3L)
                .name("asd")
                .description("test")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.getItemsByNameOrDesc("tes")).thenReturn(List.of(item1, item2, item3));

        List<Item> items = itemService.getItemsByNameOrDesc("tEs");

        assertEquals(3, items.size());
    }
}