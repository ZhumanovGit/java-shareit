package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.ItemValidateException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.ItemValidator;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemValidator itemValidator;

    @Override
    public Item createItem(Item item, long ownerId) {
        itemValidator.validateItemForCreate(item);

        if (ownerId < 0) {
            throw new ItemValidateException("Id владельца не может быть отрицательным");
        }

        User owner = userRepository.getUserById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + ownerId + " не найден"));
        item.setOwner(owner);

        return itemRepository.createItem(item);
    }

    @Override
    public Item patchItem(Item itemUpdates, long itemId, long ownerId) {
        itemValidator.validateItemForUpdate(itemUpdates);
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Объект с id = " + itemId + " не найден"));

        if (ownerId < 0) {
            throw new ItemValidateException("Id владельца не может быть отрицательным");
        }

        if (itemUpdates.getOwner().getId() != ownerId) {
            throw new ItemValidateException("Объект не может сменить владельца");
        }

        String newName = itemUpdates.getName();
        if (newName != null) {
            item.setName(newName);
        }

        String newDescription = itemUpdates.getDescription();
        if (newDescription != null) {
            item.setDescription(newDescription);
        }
        Boolean newStatus = itemUpdates.getAvailable();
        if (newStatus != null) {
            item.setAvailable(newStatus);
        }

        itemRepository.updateItem(item);
        return item;

    }

    @Override
    public Item getItemById(long id) {
        if (id < 0) {
            throw new ItemValidateException("id объекта не может быть отрицательным");
        }
        return itemRepository.getItemById(id)
                .orElseThrow(() -> new ItemNotFoundException("объект с id = " + id + " не найден"));
    }

    @Override
    public List<Item> getItems() {
        return itemRepository.getItems();
    }

    @Override
    public List<Item> getItemsByOwnerId(long ownerId) {
        if (ownerId < 0) {
            throw new ItemValidateException("Id владельца не может быть отрицательным");
        }

        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + ownerId + " не найден"));

        return itemRepository.getItemsByOwnerId(ownerId);
    }

    @Override
    public List<Item> getItemsByName(String substring) {
        String needSubstring = substring.toLowerCase();
        return itemRepository.getItemsByName(needSubstring);
    }


    @Override
    public void deleteItemById(long id) {
        itemRepository.deleteItem(id);
    }

    @Override
    public void deleteItems() {
        itemRepository.deleteAllItems();
    }
}
