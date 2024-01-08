package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemCreateDto item, long ownerId);

    ItemDto patchItem(ItemUpdateDto item, long itemId, long ownerId);

    ItemDto getItemById(long id, long requesterId);

    List<ItemInfoDto> getItemsByOwnerId(long ownerId, Pageable pageable);

    List<ItemDto> getItemsByNameOrDesc(String substring, Pageable pageable);

    void deleteItemById(long id);

    void deleteItems();
}
