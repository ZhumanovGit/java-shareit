package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.ExceptionResponse;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidateException;
import ru.practicum.shareit.exception.model.UserEmailIsAlreadyExists;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleValidateException(final ConstraintViolationException e) {
        Map<String, String> errors = e.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        f -> f.getPropertyPath().toString(),
                        f -> f.getMessage() != null ? f.getMessage() : ""));
        log.warn("ConstraintViolationExceptions, {}", errors.values());
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundException(final NotFoundException e) {
        log.warn("NotFoundException, {}", e.getMessage());
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
