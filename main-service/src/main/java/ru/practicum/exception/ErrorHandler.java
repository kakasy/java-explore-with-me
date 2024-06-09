package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

        String message = e.getMessage();
        log.info("Получен статус 400 Bad Request: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgException(final Exception e) {

        String message = e.getMessage();
        log.info("Получен статус 400 Bad Request: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException e) {

        String message = e.getMessage();
        log.info("Получен статус 404 Not Found: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {

        String message = e.getMessage();
        log.info("Получен статус 409 Conflict: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIllegalStateException(final Exception e) {

        String message = e.getMessage();
        log.info("Получен статус 409 Conflict: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(final Exception e) {

        String message = e.getMessage();
        log.info("Получен статус 409 Conflict: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.CONFLICT);
    }
}
