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
    public ApiError handleMethodArgumentNotValidException(Exception e) {
        String message = e.getMessage();
        log.info("Получен статус 400: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgException(Exception e) {
        String message = e.getMessage();
        log.info("Получен статус 400: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(Exception e) {
        String message = e.getMessage();
        log.info("Получен статус 400: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(Exception e) {
        String message = e.getMessage();
        log.info("Получен статус 404: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(Exception e) {
        String message = e.getMessage();
        log.info("Получен статус 409: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIllegalStateException(Exception e) {
        String message = e.getMessage();
        log.info("Получен статус 409: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(Exception e) {
        String message = e.getMessage();
        log.info("Получен статус 409: {}, {}", e.getMessage(), e.getStackTrace());
        return new ApiError(message, e.getCause(), HttpStatus.CONFLICT);
    }
}
