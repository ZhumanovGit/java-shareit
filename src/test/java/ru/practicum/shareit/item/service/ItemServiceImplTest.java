package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemServiceImpl itemService;
    ItemMapper mapper;

    @BeforeEach
    public void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        mapper = new ItemMapper();
        itemService = new ItemServiceImpl(itemRepository, userRepository, mapper);
    }

    void assertEqualItem(CreatedItemDto o1, CreatedItemDto o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getDescription(), o2.getDescription());
    }

    @Test
    public void createItem_whenItemIsValid_thenReturnNewItem() {
        User expectedUser = User.builder()
                .id(1L)
                .name("name")
                .email("name@e.mail")
                .build();
        ItemDto dto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        Item item = mapper.itemDtoToItem(dto);
        Item expectedItem = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(userRepository.getUserById(anyLong())).thenReturn(Optional.of(expectedUser));
        when(itemRepository.createItem(any())).thenReturn(expectedItem);

        CreatedItemDto actual = itemService.createItem(dto, expectedUser.getId());

        assertEqualItem(mapper.itemToCreatedItemDto(expectedItem), actual);
    }

    @Test
    public void createItem_whenOwnerIsNotFound_thenThrowUserNotFoundException() {
        ItemDto item = ItemDto.builder()
                .name("asd")
                .description("testDesc")
                .available(true)
                .build();
        String expectedResponse = "Пользователь с id = 5 не найден";
        when(userRepository.getUserById(5)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.createItem(item, 5));

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
        UpdateItemDto itemUpdates = UpdateItemDto.builder().name("newTest").description("new desc").available(false).build();
        when(itemRepository.getItemById(item.getId())).thenReturn(Optional.of(item));

        CreatedItemDto updatedItem = itemService.patchItem(itemUpdates, item.getId(), owner.getId());

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
        UpdateItemDto itemUpdates = UpdateItemDto.builder().description("new desc").available(false).build();
        String expectedResponse = "Объект с id = 1 не найден";

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.patchItem(itemUpdates, item.getId(), owner.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void patchItem_whenItemWasNotFound_thenThrowNotFoundException() {
        User owner = User.builder().id(1L).build();

        UpdateItemDto itemUpdates = UpdateItemDto.builder().name("asdasd").description("new desc").available(false).build();
        when(itemRepository.getItemById(2L)).thenReturn(Optional.empty());
        String expectedResponse = "Объект с id = " + 2L + " не найден";

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.patchItem(itemUpdates, 2L, owner.getId()));

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
        UpdateItemDto itemUpdates = UpdateItemDto.builder().name("asdasd").description("new desc").available(false).build();
        when(itemRepository.getItemById(1L)).thenReturn(Optional.of(item));
        String expectedResponse = "Не найден данный объект у данного пользователя";

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.patchItem(itemUpdates, item.getId(), 35));

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

        CreatedItemDto actualItem = itemService.getItemById(item.getId());

        assertEqualItem(mapper.itemToCreatedItemDto(item), actualItem);
    }

    @Test
    public void getItemById_whenItemNotFound_thenReturnItemNotFoundException() {
        String expectedResponse = "объект с id = 3 не найден";

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getItemById(3L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemsByOwnerId_whenOwnerWasNotFound_thenThrowNotFoundException() {
        String expectedResponse = "Пользователь с id = 5 не найден";
        when(userRepository.getUserById(5)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getItemsByOwnerId(5L));

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

        List<CreatedItemDto> items = itemService.getItemsByOwnerId(owner.getId());

        assertEquals(3, items.size());
    }

    @Test
    public void getItemsByNameOrDesc_whenStringIsBlank_thenReturnEmptyList() {
        List<CreatedItemDto> items = itemService.getItemsByNameOrDesc("");

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

        List<CreatedItemDto> items = itemService.getItemsByNameOrDesc("tEs");

        assertEquals(3, items.size());
    }
}