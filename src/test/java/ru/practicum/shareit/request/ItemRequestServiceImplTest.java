package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
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

class ItemRequestServiceImplTest {
    ItemRequestRepository requestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    RequestMapper mapper;
    ItemRequestServiceImpl service;

    @BeforeEach
    public void beforeEach() {
        requestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        mapper = new RequestMapper(new ItemMapper());
        service = new ItemRequestServiceImpl(requestRepository, userRepository, itemRepository, mapper);
    }

    @Test
    public void createItemRequest_whenDtoIsCorrect_thenReturnCreatedDto() {
        User owner = User.builder().id(1L).build();
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder().description("test").build();
        ItemRequest createdRequest = ItemRequest.builder()
                .id(1L)
                .description("test")
                .created(LocalDateTime.now())
                .owner(owner)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(createdRequest);

        ItemRequestDto actual = service.createItemRequest(dto, owner.getId());

        assertEquals(1L, actual.getId());
        assertEquals("test", actual.getDescription());
        assertEquals(createdRequest.getCreated(), actual.getCreated());
    }

    @Test
    public void createItemRequest_whenUserWasNotFound_thenThrowException() {
        long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        String expectedResponse = "Пользователь с id = " + userId + " не найден";

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.createItemRequest(any(), userId));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getUserRequests_whenUserWasFoundAndItemsWasnt_thenReturnListOfItemRequests() {
        User owner = User.builder().id(1L).build();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        ItemRequest first = ItemRequest.builder()
                .id(1L)
                .owner(owner)
                .description("first")
                .created(LocalDateTime.of(2020, 1, 12, 10, 10))
                .build();
        ItemRequest second = ItemRequest.builder()
                .id(2L)
                .owner(owner)
                .description("second")
                .created(LocalDateTime.of(2022, 1, 12, 10, 10))
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.findAllByOwnerId(owner.getId(), sort)).thenReturn(List.of(first, second));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of());

        List<ItemRequestInfoDto> actual = service.getAllRequests(owner.getId(), 0, 10);

        assertEquals(2, actual.size());
        assertEquals(2, actual.get(0).getId());
        assertEquals(1, actual.get(1).getId());
    }

    @Test
    public void getUserRequests_whenUserWasFoundAndItemsWas_thenReturnListOfRequestsWithItems() {
        User owner = User.builder().id(1L).build();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        ItemRequest first = ItemRequest.builder()
                .id(1L)
                .owner(owner)
                .description("first")
                .created(LocalDateTime.of(2020, 1, 12, 10, 10))
                .build();
        ItemRequest second = ItemRequest.builder()
                .id(2L)
                .owner(owner)
                .description("second")
                .created(LocalDateTime.of(2022, 1, 12, 10, 10))
                .build();
        Item firstItem = Item.builder()
                .id(1L)
                .name("first Item")
                .description("firstDesc")
                .requestId(first.getId())
                .available(true)
                .build();
        Item seoncdItem = Item.builder()
                .id(1L)
                .name("second Item")
                .description("secondDesc")
                .requestId(first.getId())
                .available(true)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.findAllByOwnerId(owner.getId(), sort)).thenReturn(List.of(first, second));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(firstItem, seoncdItem));

        List<ItemRequestInfoDto> actual = service.getUserRequests(owner.getId());

        assertEquals(2, actual.size());
        assertEquals(2, actual.get(1).getItems().size());
        assertEquals(0, actual.get(0).getItems().size());
    }

    @Test
    public void getAllRequests_whenUserWasFoundAndRequestsFoundWithoutItems_thenReturnListOfRequests() {
        User owner = User.builder().id(1L).build();
        User anotherUser = User.builder().id(2L).build();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        int from = 0;
        int size = 5;
        ItemRequest first = ItemRequest.builder()
                .id(1L)
                .owner(anotherUser)
                .description("first")
                .created(LocalDateTime.of(2020, 1, 12, 10, 10))
                .build();
        ItemRequest second = ItemRequest.builder()
                .id(2L)
                .owner(anotherUser)
                .description("second")
                .created(LocalDateTime.of(2022, 1, 12, 10, 10))
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(requestRepository.findAllByOwnerIdNot(owner.getId(), any())).thenReturn(new PageImpl<>(List.of(first, second)));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemRequestInfoDto> actual = service.getAllRequests(owner.getId(), from, size);

        assertEquals(2, actual.size());
        assertEquals(0, actual.get(0).getItems().size());
        assertEquals(0, actual.get(1).getItems().size());
    }

    @Test
    public void getAllRequests_whenUserWasFoundAndRequestsFoundWithItems_thenReturnListOfRequests() {
        User owner = User.builder().id(1L).build();
        User anotherUser = User.builder().id(2L).build();
        int from = 0;
        int size = 5;
        ItemRequest first = ItemRequest.builder()
                .id(1L)
                .owner(anotherUser)
                .description("first")
                .created(LocalDateTime.of(2020, 1, 12, 10, 10))
                .build();
        ItemRequest second = ItemRequest.builder()
                .id(2L)
                .owner(anotherUser)
                .description("second")
                .created(LocalDateTime.of(2022, 1, 12, 10, 10))
                .build();
        Item firstItem = Item.builder()
                .id(1L)
                .name("first Item")
                .description("firstDesc")
                .requestId(first.getId())
                .available(true)
                .build();
        Item seoncdItem = Item.builder()
                .id(1L)
                .name("second Item")
                .description("secondDesc")
                .requestId(first.getId())
                .available(true)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(requestRepository.findAllByOwnerIdNot(owner.getId(), any())).thenReturn(new PageImpl<>(List.of(first, second)));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(firstItem, seoncdItem));

        List<ItemRequestInfoDto> actual = service.getAllRequests(owner.getId(), from, size);

        assertEquals(2, actual.size());
        assertEquals(0, actual.get(0).getItems().size());
        assertEquals(2, actual.get(1).getItems().size());
    }

    @Test
    public void getAllRequests_whenUserWasNotFound_thenThrowException() {
        String expectedResponse = "Пользователь с id = 1 не найден";
        when(userRepository.findById(1L)).thenThrow(NotFoundException.class);

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.getAllRequests(1L, any(), any()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getRequestById_whenUserWasFoundAndRequestWasFoundWithItems_thenReturnRequest() {
        User owner = User.builder().id(1L).build();
        ItemRequest first = ItemRequest.builder()
                .id(1L)
                .owner(owner)
                .description("first")
                .created(LocalDateTime.of(2020, 1, 12, 10, 10))
                .build();
        Item firstItem = Item.builder()
                .id(1L)
                .name("first Item")
                .description("firstDesc")
                .requestId(first.getId())
                .available(true)
                .build();
        Item seoncdItem = Item.builder()
                .id(1L)
                .name("second Item")
                .description("secondDesc")
                .requestId(first.getId())
                .available(true)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(requestRepository.findById(first.getId())).thenReturn(Optional.of(first));
        when(itemRepository.findAllByRequestId(first.getId())).thenReturn(List.of(firstItem, seoncdItem));

        ItemRequestInfoDto actual = service.getRequestById(first.getId(), owner.getId());

        assertEquals(1L, actual.getId());
        assertEquals(2, actual.getItems().size());
    }

    @Test
    public void getRequestById_whenUserWasFoundAndRequestWasFoundWithoutItems_thenReturnRequest() {
        User owner = User.builder().id(1L).build();
        ItemRequest first = ItemRequest.builder()
                .id(1L)
                .owner(owner)
                .description("first")
                .created(LocalDateTime.of(2020, 1, 12, 10, 10))
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(requestRepository.findById(first.getId())).thenReturn(Optional.of(first));
        when(itemRepository.findAllByRequestId(first.getId())).thenReturn(Collections.emptyList());

        ItemRequestInfoDto actual = service.getRequestById(first.getId(), owner.getId());

        assertEquals(1L, actual.getId());
        assertEquals(0, actual.getItems().size());
    }

    @Test
    public void getRequestById_whenUserWasFoundAndRequestWastFound_thenThrowException() {
        User owner = User.builder().id(1L).build();
        String expectedResponse = "Запрос с id = 1 не найден";
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.getRequestById(1L, owner.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getRequestById_whenUserWasNotFound_thenThrowException() {
        String expectedResponse = "Пользователь с id = 1 не найден";
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.getRequestById(1L, 1L));

        assertEquals(expectedResponse, throwable.getMessage());
    }



}