package ru.practicum.shareit.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    final String error;
    String stackTrace;
}
