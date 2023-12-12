package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequest createDtoToItemRequest(ItemRequestCreateDto dto) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .build();
    }

    public ItemRequestInfoDto itemRequestToInfoDto(ItemRequest item, List<Item> items) {
        List<ItemForRequestDto> itemDtos = items.stream()
                .map(itemMapper::itemToItemInfoRequestDto)
                .collect(Collectors.toList());

        return ItemRequestInfoDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .created(item.getCreated())
                .items(itemDtos)
                .build();
    }

    public ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}
