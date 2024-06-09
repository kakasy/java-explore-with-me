package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventsController {

    private final EventService service;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    public List<EventShortDto> getPublicEventsByParam(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {

        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart))
            throw new ValidationException("Окончание события должны быть позже его начала");

        log.info("GET-запрос на получение списка событий по тексту: {}, категориям: {}, оплата: {}, начало: {}, " +
                        "конец: {}, лимит: {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable);

        return service.getPublicEventsByParam(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {

        log.info("GET-запрос на получение события с id {}", id);
        return service.getEventById(id, request);
    }
}