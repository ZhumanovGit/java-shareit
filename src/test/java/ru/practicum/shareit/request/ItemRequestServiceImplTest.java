package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    public void getUserRequests_whenUserWasNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        String expectedResponse = "Пользователь с id = 1 не найден";

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.getUserRequests(1L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getUserRequests_whenUserWasFoundAndRequestsWasNot_thenReturnEmptyList() {
        User owner = User.builder().id(1L).build();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.findAllByOwnerId(anyLong(), sort)).thenReturn(new ArrayList<>());

        List<ItemRequestInfoDto> actual = service.getUserRequests(owner.getId());

        assertEquals(0, actual.size());
    }

    @Test
    public void getAllRequests_whenUserWasNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        String expectedResponse = "Пользователь с id = 1 не найден";

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.getAllRequests(1L, 0, 10));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getAllRequests_whenUserWasFoundAndRequestsWasNot_thenReturnEmptyList() {
        User owner = User.builder().id(1L).build();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(0, 10, sort);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.findAllByOwnerIdNot(owner.getId(), pageRequest)).thenReturn(Page.empty());

        List<ItemRequestInfoDto> actual = service.getAllRequests(owner.getId(), 0, 10);

        assertEquals(0, actual.size());
    }

    @Test
    public void getAllRequests_whenUserWasFoundAndItemsWasNot_thenReturnListOfRequest() {
        User owner = User.builder().id(1L).build();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(0, 10, sort);
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
        when(requestRepository.findAllByOwnerIdNot(owner.getId(), pageRequest)).thenReturn((Page<ItemRequest>) List.of(first, second));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemRequestInfoDto> actual = service.getAllRequests(owner.getId(), 0, 10);

        assertEquals(2, actual.size());
        assertEquals(2, actual.get(0).getId());
        assertEquals(1, actual.get(1).getId());
    }

    @Test
    public void getAllRequests_whenUserWasFoundAndRequestsWasFoundAndItemsWasFound_thenReturnListOfRequests() {

    }




}