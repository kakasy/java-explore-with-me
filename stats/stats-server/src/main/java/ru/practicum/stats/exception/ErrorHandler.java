package ru.practicum.stats.exception;

import lombok.extern.slf4j.Slf4j;
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
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.info("Получен статус 400: {}, {}", ex.getMessage(), ex.getStackTrace());
        return new ErrorResponse(message, ex.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidArgumentException(InvalidArgumentException ex) {

        String message = ex.getMessage();
        log.info("Получен статус 400: {}, {}", ex.getMessage(), ex.getStackTrace());
        return new ErrorResponse(message, ex.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnhandledException(Throwable ex) {

        String message = ex.getMessage();
        log.info("Получен статус 500: {}, {}", ex.getMessage(), ex.getStackTrace());
        return new ErrorResponse(message, ex.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
