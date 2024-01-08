package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody ItemRequestCreateDto dto) {
        log.info("Обработка запроса на создание обращения от пользователя c id = {}", userId);
        return requestClient.createRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllYours(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка запроса на получение своих обращений от пользователя c id = {}", userId);
        return requestClient.getAllYours(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOthers(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "10") int size,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка запроса на получение чужих обращений с параметрами from = {}, size = {}", from, size);
        return requestClient.getAllOthers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable long requestId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка запроса на получение обращения с id = {}", requestId);
        return requestClient.getRequest(requestId, userId);
    }
}
