package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper mapper;


    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestCreateDto dto, long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        ItemRequest request = mapper.createDtoToItemRequest(dto);
        request.setCreated(LocalDateTime.now());
        request.setOwner(owner);
        ItemRequest createdRequest = requestRepository.save(request);
        return mapper.itemRequestToDto(createdRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestInfoDto> getUserRequests(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        List<ItemRequest> requests = requestRepository.findAllByOwnerId(userId,
                Sort.by(Sort.Direction.DESC, "created"));

        return setItemsToRequests(requests);
    }

    @Override
    public List<ItemRequestInfoDto> getAllRequests(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        List<ItemRequest> requests = requestRepository.findAllByOwnerIdNot(userId, pageable);

        return setItemsToRequests(requests);
    }

    @Override
    public ItemRequestInfoDto getRequestById(long id, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        ItemRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + id + " не найден"));

        List<Item> items = itemRepository.findAllByRequestId(id);

        if (items.isEmpty()) {
            return mapper.itemRequestToInfoDto(request, new ArrayList<>());
        }
        return mapper.itemRequestToInfoDto(request, items);
    }

    private List<ItemRequestInfoDto> setItemsToRequests(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> requestItemsMap = new HashMap<>();
        for (Item item : items) {
            requestItemsMap.computeIfAbsent(item.getRequestId(), k -> new ArrayList<>()).add(item);
        }

        List<ItemRequestInfoDto> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            Long requestId = request.getId();
            List<Item> requestItems = requestItemsMap.getOrDefault(requestId, new ArrayList<>());
            result.add(mapper.itemRequestToInfoDto(request, requestItems));
        }
        return result;
    }
}
