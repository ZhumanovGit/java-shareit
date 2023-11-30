package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreatedCommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ManyCreatedItemsDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping
    public List<ManyCreatedItemsDto> getAllUserItems(@RequestHeader(value = "X-Sharer-User-Id") long ownerId) {
        log.info("Обработка запроса на получение всех вещей пользователя с id = {}", ownerId);
        List<ManyCreatedItemsDto> items = itemService.getItemsByOwnerId(ownerId);
        log.info("Получены все вещи пользователя с id = {}", ownerId);
        return items;
    }

    @GetMapping("/{itemId}")
    public CreatedItemDto getItemById(@PathVariable long itemId,
                                      @RequestHeader(value = "X-Sharer-User-Id") long requesterId) {
        log.info("Обработка запроса на получение вещи с id = {}", itemId);
        CreatedItemDto dto = itemService.getItemById(itemId, requesterId);
        log.info("Получена вещь с id = {}", dto.getId());
        return dto;
    }

    @GetMapping("/search")
    public List<CreatedItemDto> searchItems(@RequestParam(value = "text") String text) {
        log.info("Обработка запроса на выполнение поиска по строке {}", text);
        List<CreatedItemDto> items = itemService.getItemsByNameOrDesc(text);
        log.info("Получен список длиной {}", items.size());
        return items;
    }

    @PostMapping
    public CreatedItemDto createItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @Valid @RequestBody ItemDto item) {
        log.info("Обработка запроса на создание новой вещи пользователем с id = {}", ownerId);
        CreatedItemDto dto = itemService.createItem(item, ownerId);
        log.info("создана новая вешь с id = {}", dto.getId());
        return dto;
    }

    @PatchMapping("/{itemId}")
    public CreatedItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                    @PathVariable long itemId,
                                    @RequestBody UpdateItemDto item) {
        log.info("Обработка запроса на частичное обновление вещи с id = {} пользователем с id = {}", itemId, ownerId);
        CreatedItemDto updatedItem = itemService.patchItem(item, itemId, ownerId);
        log.info("Обновлены параметры вещи с id = {}", updatedItem.getId());
        return updatedItem;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable long itemId) {
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

    @PostMapping("/{itemId}/comment")
    public CreatedCommentDto postComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                         @PathVariable long itemId,
                                         @Valid @RequestBody CommentDto dto) {
        log.info("Обработка запроса на создание коментария к вещи с id = {} пользователем с id = {}", itemId, authorId);
        CreatedCommentDto comment = commentService.createNewComment(dto, itemId, authorId);
        log.info("Создан комментарий с id = {}", comment.getId());
        return comment;
    }


}
