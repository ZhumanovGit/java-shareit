package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Item item, long ownerId);

    Item patchItem(Item item, long itemId, long ownerId);

    Item getItemById(long id);

    List<Item> getItems();

    List<Item> getItemsByOwnerId(long ownerId);

    List<Item> getItemsByName(String substring);

    void deleteItemById(long id);

    void deleteItems();
}
