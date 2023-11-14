package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.ExceptionResponse;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.ItemValidateException;
import ru.practicum.shareit.exception.model.UserEmailIsAlreadyExists;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.exception.model.UserValidateException;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler(ItemValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleItemValidateException(final ItemValidateException e) {
        log.warn("ItemValidateException, {}", e.getMessage());
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(UserValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleUserValidateException(final UserValidateException e) {
        log.warn("UserValidateException, {}", e.getMessage());
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleItemNotFoundException(final ItemNotFoundException e) {
        log.warn("ItemNotFoundException, {}", e.getMessage());
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleUserNotFoundException(final UserNotFoundException e) {
        log.warn("UserNotFoundException, {}", e.getMessage());
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(UserEmailIsAlreadyExists.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleUserEmailIsAlreadyExists(final UserEmailIsAlreadyExists e) {
        log.warn("UserEmailIsAlreadyExists, {}", e.getMessage());
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleOtherExceptions(final RuntimeException e) {
        log.warn("Внутреннее исключение {}", e.getMessage(), e);
        return new ExceptionResponse(e.getMessage(), printStackTrace(e));
    }

    private String printStackTrace(RuntimeException e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
