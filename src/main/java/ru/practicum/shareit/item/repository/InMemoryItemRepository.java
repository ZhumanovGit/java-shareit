package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    private static long id;

    private long increaseId() {
        return ++id;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(increaseId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void updateItem(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> getItemById(long id) {
        return Optional.of(items.get(id));
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getItemsByOwnerId(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByNameOrDesc(String string) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(string)
                        || item.getDescription().toLowerCase().contains(string))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteAllItemsByOwnerId(long ownerId) {
        List<Item> needItems = getItemsByOwnerId(ownerId);

        for (Item i : needItems) {
            items.put(i.getId(), null);
        }
    }

    @Override
    public void deleteItem(long id) {
        items.put(id, null);
    }

    @Override
    public void deleteAllItems() {
        items.clear();
    }
}
