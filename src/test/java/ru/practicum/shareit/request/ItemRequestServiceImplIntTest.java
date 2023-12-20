package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void createItemRequest_whenDataIsCorrect_thenReturnNewItemRequest() {
        User user = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        ItemRequestCreateDto requestCreateDto = ItemRequestCreateDto.builder()
                .description("desc")
                .build();

        ItemRequestDto createdRequestDto = itemRequestService.createItemRequest(requestCreateDto, user.getId());

        assertNotNull(createdRequestDto);
        assertNotNull(createdRequestDto.getId());
        assertEquals(requestCreateDto.getDescription(), createdRequestDto.getDescription());
        assertNotNull(createdRequestDto.getCreated());
    }

    @Test
    void getUserRequests_whenDataIsCorrect_thenReturnListOfRequests() {
        User user = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        ItemRequest request = createSampleRequest(user);
        itemRepository.save(Item.builder()
                .requestId(request.getId())
                .owner(user)
                .available(true)
                .name("name")
                .description("desc")
                .build());
        itemRepository.save(Item.builder()
                .requestId(request.getId())
                .owner(user)
                .available(true)
                .name("name2")
                .description("desc2")
                .build());

        List<ItemRequestInfoDto> userRequests = itemRequestService.getUserRequests(user.getId());
        ItemRequestInfoDto requestInfoDto = userRequests.get(0);

        assertNotNull(userRequests);
        assertEquals(1, userRequests.size());
        assertNotNull(requestInfoDto);
        assertEquals(request.getId(), requestInfoDto.getId());
        assertEquals(request.getDescription(), requestInfoDto.getDescription());
        assertNotNull(requestInfoDto.getCreated());
        assertFalse(requestInfoDto.getItems().isEmpty());
    }

    @Test
    void getAllRequests_whenDataIsCorrect_thenReturnListOfRequests() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice2@email.com")
                .build());
        User user = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .owner(owner)
                .description("desc")
                .created(LocalDateTime.now())
                .build());
        itemRepository.save(Item.builder()
                .requestId(request.getId())
                .owner(owner)
                .available(true)
                .name("name")
                .description("desc")
                .build());
        itemRepository.save(Item.builder()
                .requestId(request.getId())
                .owner(owner)
                .available(true)
                .name("name2")
                .description("desc2")
                .build());

        List<ItemRequestInfoDto> allRequests = itemRequestService.getAllRequests(user.getId(), 0, 10);
        ItemRequestInfoDto requestInfoDto = allRequests.get(0);

        // Assert
        assertNotNull(allRequests);
        assertNotNull(requestInfoDto);
        assertEquals(request.getId(), requestInfoDto.getId());
        assertEquals(request.getDescription(), requestInfoDto.getDescription());
        assertNotNull(requestInfoDto.getCreated());
        assertFalse(requestInfoDto.getItems().isEmpty());
    }

    @Test
    void getRequestById_whenDataIsCorrect_thenReturnNeedRequest() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("nice2@email.com")
                .build());
        User user = userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        ItemRequest request = createSampleRequest(user);
        itemRepository.save(Item.builder()
                .requestId(request.getId())
                .owner(owner)
                .available(true)
                .name("name")
                .description("desc")
                .build());
        itemRepository.save(Item.builder()
                .requestId(request.getId())
                .owner(owner)
                .available(true)
                .name("name2")
                .description("desc2")
                .build());

        ItemRequestInfoDto requestInfoDto = itemRequestService.getRequestById(request.getId(), user.getId());

        assertNotNull(requestInfoDto);
        assertEquals(request.getId(), requestInfoDto.getId());
        assertEquals(request.getDescription(), requestInfoDto.getDescription());
        assertNotNull(requestInfoDto.getCreated());
        assertFalse(requestInfoDto.getItems().isEmpty());
    }

    private ItemRequest createSampleRequest(User user) {
        ItemRequest request = ItemRequest.builder()
                .owner(user)
                .created(LocalDateTime.now())
                .description("test")
                .build();

        return requestRepository.save(request);
    }
}

