package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @Valid @RequestBody ItemRequestCreateDto dto) {
        log.info("Обработка запроса на создание обращения");
        ItemRequestDto result = service.createItemRequest(dto, userId);
        log.info("Создано новое обращение с id = {}", result.getId());
        return result;
    }

    @GetMapping
    public List<ItemRequestInfoDto> getAllYours(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка запроса на получение своих обращений");
        List<ItemRequestInfoDto> result = service.getUserRequests(userId);
        log.info("Получен список обращений длиной {}", result.size());
        return result;
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAllOthers(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "1") int size,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка запроса на получение чужих обращений");
        List<ItemRequestInfoDto> result = service.getAllRequests(userId, from, size);
        log.info("Получен список всех обращений длиной {}", result.size());
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getRequestById(@PathVariable int requestId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка запроса на получение обращения с id = {}", requestId);
        ItemRequestInfoDto result = service.getRequestById(requestId, userId);
        log.info("Получено обращение с id = {}", result.getId());
        return result;
    }
}
