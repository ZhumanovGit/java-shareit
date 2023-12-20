package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.BookingException;
import ru.practicum.shareit.exception.model.ExceptionResponse;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.PostCommentException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException, {}", e.getMessage());
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundException(final NotFoundException e) {
        log.warn("NotFoundException, {}", e.getMessage());
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(BookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleItemBookedException(final BookingException e) {
        log.warn("ItemBookedException, {}", e.getMessage());
        return new ExceptionResponse(e.getMessage());

    }

    @ExceptionHandler(PostCommentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handlePostCommentException(final PostCommentException e) {
        log.warn("PostCommentException {}", e.getMessage());
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleOtherExceptions(final Exception e) {
        log.warn("Внутреннее исключение {}", e.getMessage(), e);
        return new ExceptionResponse(e.getMessage(), printStackTrace(e));
    }

    private String printStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
