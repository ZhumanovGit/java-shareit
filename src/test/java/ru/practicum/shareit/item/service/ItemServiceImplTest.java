package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemServiceImpl itemService;
    ItemMapper mapper;
    BookingMapper bookingMapper;
    CommentMapper commentMapper;

    @BeforeEach
    public void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        mapper = new ItemMapper();
        bookingMapper = new BookingMapper();
        commentMapper = new CommentMapper();
        itemService = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                mapper,
                bookingMapper,
                commentMapper);
    }

    void assertEqualItem(ItemDto o1, ItemDto o2) {
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
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        Item item = mapper.itemCreateDtoToItem(dto);
        Item expectedItem = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(expectedUser));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        ItemDto actual = itemService.createItem(dto, expectedUser.getId());

        assertEqualItem(mapper.itemToItemDto(expectedItem), actual);
    }

    @Test
    public void createItem_whenOwnerIsNotFound_thenThrowUserNotFoundException() {
        ItemCreateDto item = ItemCreateDto.builder()
                .name("asd")
                .description("testDesc")
                .available(true)
                .build();
        String expectedResponse = "Пользователь с id = 5 не найден";
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

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
        ItemUpdateDto itemUpdates = ItemUpdateDto.builder().name("newTest").description("new desc").available(false).build();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDto updatedItem = itemService.patchItem(itemUpdates, item.getId(), owner.getId());

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
        ItemUpdateDto itemUpdates = ItemUpdateDto.builder().description("new desc").available(false).build();
        String expectedResponse = "Объект с id = 1 не найден";

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.patchItem(itemUpdates, item.getId(), owner.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void patchItem_whenItemWasNotFound_thenThrowNotFoundException() {
        User owner = User.builder().id(1L).build();

        ItemUpdateDto itemUpdates = ItemUpdateDto.builder().name("asdasd").description("new desc").available(false).build();
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());
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
        ItemUpdateDto itemUpdates = ItemUpdateDto.builder().name("asdasd").description("new desc").available(false).build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        String expectedResponse = "Не найден данный объект у данного пользователя";

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.patchItem(itemUpdates, item.getId(), 35));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void findById_whenOwnerItemWasFoundAndItemNotHaveBookings_returnItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.ASC, "created"))).thenReturn(new ArrayList<>());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusIs(item.getId(),
                        LocalDateTime.now(),
                        BookingStatus.APPROVED,
                        Sort.by(Sort.Direction.DESC, "start"))).thenReturn(Optional.empty());
        when(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusIsNot(item.getId(),
                        LocalDateTime.now(),
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.ASC, "start"))).thenReturn(Optional.empty());

        ItemDto actualItem = itemService.getItemById(item.getId(), owner.getId());

        assertEqualItem(mapper.itemToItemDto(item), actualItem);
    }

    @Test
    public void findById_whenItemWasFoundFromNotOwner_returnItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        when(userRepository.findById(25L)).thenReturn(Optional.of(owner));
        when(commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.ASC, "created"))).thenReturn(new ArrayList<>());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusIs(item.getId(),
                        LocalDateTime.now(),
                        BookingStatus.APPROVED,
                        Sort.by(Sort.Direction.DESC, "start"))).thenReturn(Optional.empty());
        when(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusIsNot(item.getId(),
                        LocalDateTime.now(),
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.ASC, "start"))).thenReturn(Optional.empty());

        ItemDto actualItem = itemService.getItemById(item.getId(), 25L);

        assertEqualItem(mapper.itemToItemDto(item), actualItem);
    }

    @Test
    public void findById_whenItemNotFound_thenReturnItemNotFoundException() {
        String expectedResponse = "объект с id = 3 не найден";
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getItemById(3L, 1L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void findById_whenRequesterNotFound_thenThrowNotFoundException() {
        String expectedResponse = "Пользователь с id = 3 не найден";
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 3L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemsByOwnerId_whenOwnerWasNotFound_thenThrowNotFoundException() {
        String expectedResponse = "Пользователь с id = 5 не найден";
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

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
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1, item2, item3));
        when(bookingRepository.findAllByItemIdInAndStartBeforeAndStatusIs(List.of(1L, 2L, 3L),
                LocalDateTime.now(), BookingStatus.APPROVED)).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemIdInAndStartAfterAndStatusIsNot(List.of(1L, 2L, 3L),
                LocalDateTime.now(), BookingStatus.REJECTED)).thenReturn(new ArrayList<>());

        List<ItemInfoDto> items = itemService.getItemsByOwnerId(owner.getId());

        assertEquals(3, items.size());
    }

    @Test
    public void getItemsByNameOrDesc_whenStringIsBlank_thenReturnEmptyList() {
        List<ItemDto> items = itemService.getItemsByNameOrDesc("");

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
        when(itemRepository.findAllByNameOrDesc("tes")).thenReturn(List.of(item1, item2, item3));

        List<ItemDto> items = itemService.getItemsByNameOrDesc("tEs");

        assertEquals(3, items.size());
    }
}