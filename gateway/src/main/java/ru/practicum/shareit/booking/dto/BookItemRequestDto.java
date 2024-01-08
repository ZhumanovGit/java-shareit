package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull(message = "Не передан объект аренды")
    private Long itemId;
    @NotNull(message = "Не передана дата начала аренды")
    @FutureOrPresent(message = "Время начала аренды в прошлом")
    private LocalDateTime start;
    @NotNull(message = "Не передана дата конца аренды")
    @Future(message = "Время окончания аренды в прошлом")
    private LocalDateTime end;
}