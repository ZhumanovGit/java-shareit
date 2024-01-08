package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Обработка запроса на получение всех вещей пользователя с id = {} и параметрами from={}, size={}",
                ownerId, from, size);
        return itemClient.getUserItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(value = "X-Sharer-User-Id") long requesterId) {
        log.info("Обработка запроса на получение вещи с id = {}", itemId);
        return itemClient.getItem(requesterId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(value = "text") String text,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Positive @RequestParam(defaultValue = "10") int size,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка запроса на выполнение поиска по строке {} c параметрами from = {}, size = {} от пользоввателя с id = {}",
                text, from, size, userId);
        return itemClient.searchItems(text, userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @Valid @RequestBody ItemCreateDto item) {
        log.info("Обработка запроса на создание новой вещи пользователем с id = {}", ownerId);
        return itemClient.createItem(ownerId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                            @PathVariable long itemId,
                                            @RequestBody ItemUpdateDto item) {
        log.info("Обработка запроса на частичное обновление вещи с id = {} пользователем с id = {}", itemId, ownerId);
        return itemClient.patchItem(ownerId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        log.info("Обработка запроса на удаление вещи с id = {}", itemId);
        itemClient.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                              @PathVariable long itemId,
                                              @Valid @RequestBody CommentCreateDto dto) {
        log.info("Обработка запроса на создание коментария к вещи с id = {} пользователем с id = {}", itemId, authorId);
        return itemClient.postComment(authorId, itemId, dto);
    }
}
