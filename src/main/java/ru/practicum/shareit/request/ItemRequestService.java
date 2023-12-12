package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {

    public ItemRequestDto createItemRequest(ItemRequestCreateDto dto, long userId);

    public List<ItemRequestInfoDto> getUserRequests(long userId);

    public List<ItemRequestInfoDto> getAllRequests(long userId, int from, int size);

    public ItemRequestInfoDto getRequestById(long id, long userId);
}
