package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.Comment;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository requestRepository;
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
        requestRepository = mock(ItemRequestRepository.class);
        mapper = new ItemMapper();
        bookingMapper = new BookingMapper();
        commentMapper = new CommentMapper();
        itemService = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                requestRepository,
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
    public void createItem_whenItemIsValidWithNoRequestId_thenReturnNewItem() {
        User expectedUser = User.builder()
                .id(1L)
                .build();
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
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
    public void createItem_whenItemIsValidWithRequestIdWasFound_thenReturnNewItemWithRequestId() {
        User expectedUser = User.builder()
                .id(1L)
                .build();
        ItemRequest request = ItemRequest.builder().id(1L).build();
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("name")
                .requestId(request.getId())
                .description("description")
                .available(true)
                .build();
        Item expectedItem = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(request.getId())
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(expectedUser));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        ItemDto actual = itemService.createItem(dto, expectedUser.getId());

        assertEqualItem(mapper.itemToItemDto(expectedItem), actual);
        assertEquals(request.getId(), actual.getRequestId());
    }

    @Test
    public void createItem_whenItemIsValidWithRequestIdNotFound_thenThrowException() {
        User expectedUser = User.builder()
                .id(1L)
                .build();
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("name")
                .requestId(1L)
                .description("description")
                .available(true)
                .build();
        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));
        when(requestRepository.findById(any())).thenReturn(Optional.empty());
        String expectedResponse = "Запроса с id = 1 не существует";

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.createItem(dto, expectedUser.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
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
    public void patchItem_whenItemUpdatesAreCorrectAndNameNull_thenReturnUpdatedItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        ItemUpdateDto itemUpdates = ItemUpdateDto.builder().description("new desc").available(false).build();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDto updatedItem = itemService.patchItem(itemUpdates, item.getId(), owner.getId());

        assertEquals(item.getId(), updatedItem.getId());
        assertEquals(item.getName(), updatedItem.getName());
        assertEquals(itemUpdates.getDescription(), updatedItem.getDescription());
        assertEquals(itemUpdates.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    public void patchItem_whenItemUpdateAreCorrectAndDescriptionNull_thenReturnUpdatedItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        ItemUpdateDto itemUpdates = ItemUpdateDto.builder().name("newTest").available(false).build();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDto updatedItem = itemService.patchItem(itemUpdates, item.getId(), owner.getId());

        assertEquals(item.getId(), updatedItem.getId());
        assertEquals(itemUpdates.getName(), updatedItem.getName());
        assertEquals(item.getDescription(), updatedItem.getDescription());
        assertEquals(itemUpdates.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    public void patchItem_whenItemUpdatesAreCorrectAndAvailableNull_thenReturnUpdatedItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        ItemUpdateDto itemUpdates = ItemUpdateDto.builder().name("newTest").description("new desc").build();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDto updatedItem = itemService.patchItem(itemUpdates, item.getId(), owner.getId());

        assertEquals(item.getId(), updatedItem.getId());
        assertEquals(itemUpdates.getName(), updatedItem.getName());
        assertEquals(itemUpdates.getDescription(), updatedItem.getDescription());
        assertEquals(item.getAvailable(), updatedItem.getAvailable());
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
    public void getItemById_whenOwnerItemWasFoundAndItemNotHaveBookingsAndNotHaveComments_returnItem() {
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
                Sort.by(Sort.Direction.ASC, "created"))).thenReturn(Collections.emptyList());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findFirstBy(any(BooleanExpression.class),
                        any(Sort.class))).thenReturn(Optional.empty());
        when(bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusIs(anyLong(),
                        any(LocalDateTime.class),
                        any(BookingStatus.class),
                        any(Sort.class)))
                .thenReturn(Optional.empty());
        when(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusIsNot(anyLong(),
                        any(LocalDateTime.class),
                        any(BookingStatus.class),
                        any(Sort.class)))
                .thenReturn(Optional.empty());

        ItemDto actualItem = itemService.getItemById(item.getId(), owner.getId());

        assertEqualItem(mapper.itemToItemDto(item), actualItem);
    }

    @Test
    public void getItemById_whenOwnerItemWasFoundAndItemHaveBookingsAndComments_returnItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Booking last = Booking.builder().id(1L).booker(User.builder().id(2L).build()).build();
        Booking next = Booking.builder().id(2L).booker(User.builder().id(2L).build()).build();
        Comment comment = Comment.builder().id(1L).author(User.builder().name("author").build()).build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.ASC, "created"))).thenReturn(List.of(comment));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusIs(anyLong(),
                        any(LocalDateTime.class),
                        any(BookingStatus.class),
                        any(Sort.class)))
                .thenReturn(Optional.of(last));
        when(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusIsNot(anyLong(),
                        any(LocalDateTime.class),
                        any(BookingStatus.class),
                        any(Sort.class)))
                .thenReturn(Optional.of(next));

        ItemDto actualItem = itemService.getItemById(item.getId(), owner.getId());

        assertEqualItem(mapper.itemToItemDto(item), actualItem);
        assertEquals(last.getId(), actualItem.getLastBooking().getId());
        assertEquals(1, actualItem.getComments().size());
    }

    @Test
    public void getItemById_whenUserAndItemAndBookingsWasFoundAndCommentsWasNot_thenReturnItem() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        Booking last = Booking.builder().id(1L).booker(User.builder().id(2L).build()).build();
        Booking next = Booking.builder().id(2L).booker(User.builder().id(2L).build()).build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.ASC, "created"))).thenReturn(Collections.emptyList());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusIs(anyLong(),
                        any(LocalDateTime.class),
                        any(BookingStatus.class),
                        any(Sort.class)))
                .thenReturn(Optional.of(last));
        when(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusIsNot(anyLong(),
                        any(LocalDateTime.class),
                        any(BookingStatus.class),
                        any(Sort.class)))
                .thenReturn(Optional.of(next));

        ItemDto actualItem = itemService.getItemById(item.getId(), owner.getId());

        assertEqualItem(mapper.itemToItemDto(item), actualItem);
        assertEquals(last.getId(), actualItem.getLastBooking().getId());
        assertEquals(0, actualItem.getComments().size());
    }

    @Test
    public void getItemById_whenRequesterIsNotOwner_thenReturnItemWithoutBookings() {
        User requester = User.builder().id(2L).build();
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("asd")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build();
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.ASC, "created"))).thenReturn(Collections.emptyList());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDto actualItem = itemService.getItemById(item.getId(), requester.getId());

        assertEqualItem(mapper.itemToItemDto(item), actualItem);
        assertEquals(0, actualItem.getComments().size());
    }

    @Test
    public void getItemById_whenItemNotFound_thenReturnItemNotFoundException() {
        String expectedResponse = "объект с id = 3 не найден";
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getItemById(3L, 1L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemById_whenRequesterNotFound_thenThrowNotFoundException() {
        String expectedResponse = "Пользователь с id = 3 не найден";
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 3L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemsByOwnerId_whenOwnerWasNotFound_thenThrowNotFoundException() {
        String expectedResponse = "Пользователь с id = 5 не найден";
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getItemsByOwnerId(5L, PageRequest.of(0, 10)));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getItemsByOwnerId_whenItemsWasNotFound_thenReturnEmptyList() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(Collections.emptyList());
        List<ItemInfoDto> dto = itemService.getItemsByOwnerId(user.getId(), PageRequest.of(0, 10));

        assertEquals(0, dto.size());
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
        PageRequest request = PageRequest.of(0, 10);
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId(), request)).thenReturn(List.of(item1, item2, item3));
        when(bookingRepository.findAll(any(BooleanExpression.class))).thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemIdInAndStartBeforeAndStatusIs(anyList(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemIdInAndStartAfterAndStatusIsNot(anyList(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class))).thenReturn(Collections.emptyList());

        List<ItemInfoDto> items = itemService.getItemsByOwnerId(owner.getId(), PageRequest.of(0, 10));

        assertEquals(3, items.size());
    }

    @Test
    public void getItemsByOwnerId_whenOwnerIsCorrectAndItemHasBookings_thenReturnListOfItems() {
        User owner = User.builder().id(1L).build();
        User booker = User.builder().id(2L).build();
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
        Booking booking1 = Booking.builder()
                .id(1L)
                .item(item1)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item1)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        Booking booking3 = Booking.builder()
                .id(3L)
                .item(item2)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Booking booking4 = Booking.builder()
                .id(4L)
                .item(item2)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Booking booking5 = Booking.builder()
                .id(5L)
                .item(item1)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Booking booking6 = Booking.builder()
                .id(6L)
                .item(item1)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now())
                .build();
        List<Booking> bookings = List.of(booking1, booking2, booking3, booking4, booking5, booking6);
        PageRequest request = PageRequest.of(0, 10);
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId(), request)).thenReturn(List.of(item1, item2, item3));
        when(bookingRepository.findAllByItemIdInAndStartBeforeAndStatusIs(anyList(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(bookings);
        when(bookingRepository.findAllByItemIdInAndStartAfterAndStatusIsNot(anyList(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(bookings);
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class))).thenReturn(Collections.emptyList());


        List<ItemInfoDto> items = itemService.getItemsByOwnerId(owner.getId(), PageRequest.of(0, 10));

        assertEquals(3, items.size());

    }

    @Test
    public void getItemsByNameOrDesc_whenStringIsBlank_thenReturnEmptyList() {
        List<ItemDto> items = itemService.getItemsByNameOrDesc("", PageRequest.of(0, 10));

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
        PageRequest request = PageRequest.of(0, 10);
        when(itemRepository.findAllByNameOrDesc("tes", request)).thenReturn(List.of(item1, item2, item3));

        List<ItemDto> items = itemService.getItemsByNameOrDesc("tEs", PageRequest.of(0, 10));

        assertEquals(3, items.size());
    }
}