package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ManyCreatedItemsDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    CreatedItemDto createItem(ItemDto item, long ownerId);

    CreatedItemDto patchItem(UpdateItemDto item, long itemId, long ownerId);

    CreatedItemDto getItemById(long id, long requesterId);

    List<ManyCreatedItemsDto> getItemsByOwnerId(long ownerId);

    List<CreatedItemDto> getItemsByNameOrDesc(String substring);

    void deleteItemById(long id);

    void deleteItems();
}
