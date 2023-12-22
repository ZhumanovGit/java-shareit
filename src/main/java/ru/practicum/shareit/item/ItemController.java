package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping
    public List<ItemInfoDto> getAllUserItems(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        PageRequest request = PageRequest.of(0, size);
        if (from >= size) {
            request = PageRequest.of(((from + 1) % size == 0 ? ((from + 1) / size) - 1 : (from + 1) / size), size);
        }
        log.info("Обработка запроса на получение всех вещей пользователя с id = {}", ownerId);
        List<ItemInfoDto> items = itemService.getItemsByOwnerId(ownerId, request);
        log.info("Получены все вещи пользователя с id = {}", ownerId);
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader(value = "X-Sharer-User-Id") long requesterId) {
        log.info("Обработка запроса на получение вещи с id = {}", itemId);
        ItemDto dto = itemService.getItemById(itemId, requesterId);
        log.info("Получена вещь с id = {}", dto.getId());
        return dto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                     @Positive @RequestParam(defaultValue = "10") int size) {
        PageRequest request = PageRequest.of(0, size);
        if (from >= size) {
            request = PageRequest.of(((from + 1) % size == 0 ? ((from + 1) / size) - 1 : (from + 1) / size), size);
        }
        log.info("Обработка запроса на выполнение поиска по строке {}", text);
        List<ItemDto> items = itemService.getItemsByNameOrDesc(text, request);
        log.info("Получен список длиной {}", items.size());
        return items;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @Valid @RequestBody ItemCreateDto item) {
        log.info("Обработка запроса на создание новой вещи пользователем с id = {}", ownerId);
        ItemDto dto = itemService.createItem(item, ownerId);
        log.info("создана новая вешь с id = {}", dto.getId());
        return dto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                             @PathVariable long itemId,
                             @RequestBody ItemUpdateDto item) {
        log.info("Обработка запроса на частичное обновление вещи с id = {} пользователем с id = {}", itemId, ownerId);
        ItemDto updatedItem = itemService.patchItem(item, itemId, ownerId);
        log.info("Обновлены параметры вещи с id = {}", updatedItem.getId());
        return updatedItem;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable long itemId) {
        log.info("Обработка запроса на удаление вещи с id = {}", itemId);
        itemService.deleteItemById(itemId);
        log.info("Удалена вешь с id = {}", itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                  @PathVariable long itemId,
                                  @Valid @RequestBody CommentCreateDto dto) {
        log.info("Обработка запроса на создание коментария к вещи с id = {} пользователем с id = {}", itemId, authorId);
        CommentDto comment = commentService.createNewComment(dto, itemId, authorId);
        log.info("Создан комментарий с id = {}", comment.getId());
        return comment;
    }
}
