package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item createItem(Item item);

    void updateItem(Item item);

    Optional<Item> getItemById(long id);

    List<Item> getItems();

    List<Item> getItemsByOwnerId(long ownerId);

    List<Item> getItemsByName(String name);

    void deleteAllItemsByOwnerId(long ownerId);

    void deleteItem(long id);

    void deleteAllItems();
}
