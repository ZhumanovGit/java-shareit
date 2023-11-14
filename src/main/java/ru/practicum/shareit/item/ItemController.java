package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper mapper;

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        List<Item> items;
        if (ownerId == null) {
            log.info("Обработка запроса на получение всех вещей");
            items = itemService.getItems();
            log.info("Получены все вещи");
        } else {
            log.info("Обработка запроса на получение всез вещей пользователя с id = {}}", ownerId);
            items = itemService.getItemsByOwnerId(ownerId);
            log.info("Получены все вещи пользователя с id = {}", ownerId);
        }
        return items.stream()
                .map(mapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Обработка запроса на получение вещи с id = {}", itemId);
        ItemDto dto = mapper.itemToItemDto(itemService.getItemById(itemId));
        log.info("Получена вещь с id = {}", dto.getId());
        return dto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text) {
        log.info("Обработка запроса на выполнение поиска по строке {}", text);
        List<ItemDto> items = itemService.getItemsByName(text).stream()
                .map(mapper::itemToItemDto)
                .collect(Collectors.toList());
        log.info("Получен список длиной {}", items.size());
        return items;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody Item item) {
        log.info("Обработка запроса на создание новой вещи пользователем с id = {}", ownerId);
        ItemDto dto = mapper.itemToItemDto(itemService.createItem(item, ownerId));
        log.info("создана новая вешь с id = {}", dto.getId());
        return dto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @PathVariable long itemId,
                             @RequestBody Item item) {
        log.info("Обработка запроса на частичное обновление вещи с id = {} пользователем с id = {}", itemId, ownerId);
        Item updatedItem = itemService.patchItem(item, itemId, ownerId);
        log.info("Обновлены параметры вещи с id = {}", updatedItem.getId());
        return mapper.itemToItemDto(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable Long itemId) {
        log.info("Обработка запроса на удаление вещи с id = {}", itemId);
        itemService.deleteItemById(itemId);
        log.info("Удалена вешь с id = {}", itemId);
    }

    @DeleteMapping
    public void deleteItems() {
        log.info("Обработка запроса на удаление всех вещей");
        itemService.deleteItems();
        log.info("Все вещи удалены");
    }


}
