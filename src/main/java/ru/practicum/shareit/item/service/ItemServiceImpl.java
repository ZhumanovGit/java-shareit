package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public CreatedItemDto createItem(ItemDto dto, long ownerId) {

        User owner = userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));

        Item item = mapper.itemDtoToItem(dto);
        item.setOwner(owner);
        Item createdItem = itemRepository.createItem(item);
        return mapper.itemToCreatedItemDto(createdItem);
    }

    @Override
    public CreatedItemDto patchItem(@NonNull UpdateItemDto itemUpdates, long itemId, long ownerId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Объект с id = " + itemId + " не найден"));

        if (item.getOwner().getId() != ownerId) {
            throw new NotFoundException("Не найден данный объект у данного пользователя");
        }

        if (itemUpdates.getName() == null) {
            itemUpdates.setName(item.getName());
        }

        if (itemUpdates.getDescription() == null) {
            itemUpdates.setDescription(item.getDescription());
        }

        if (itemUpdates.getAvailable() == null) {
            itemUpdates.setAvailable(item.getAvailable());
        }

        Item itemForUpdate = mapper.updateItemDtoToItem(itemUpdates);
        itemForUpdate.setId(itemId);
        itemForUpdate.setOwner(item.getOwner());

        itemRepository.updateItem(itemForUpdate);
        return mapper.itemToCreatedItemDto(itemForUpdate);

    }

    @Override
    public CreatedItemDto getItemById(long id) {
        Item item = itemRepository.getItemById(id)
                .orElseThrow(() -> new NotFoundException("объект с id = " + id + " не найден"));
        return mapper.itemToCreatedItemDto(item);
    }

    @Override
    public List<CreatedItemDto> getItemsByOwnerId(long ownerId) {

        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));

        List<Item> items = itemRepository.getItemsByOwnerId(ownerId);

        return items.stream()
                .map(mapper::itemToCreatedItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CreatedItemDto> getItemsByNameOrDesc(String substring) {
        if (substring.isBlank()) {
            return new ArrayList<>();
        }
        String needSubstring = substring.toLowerCase();

        List<Item> items = itemRepository.getItemsByNameOrDesc(needSubstring);
        return items.stream()
                .map(mapper::itemToCreatedItemDto)
                .collect(Collectors.toList());
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
