package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventsController {

    private final EventService service;

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос на получение списка событий от пользователя с id {}", userId);
        return service.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdAndUser(@PathVariable Long userId, @PathVariable Long eventId) {

        log.info("GET-запрос на получение события с id {} от пользователя с id {}", eventId, userId);
        return service.getEventByIdAndUser(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable Long userId, @PathVariable Long eventId) {

        log.info("GET-запрос на получение запросов на участии в событии с id {} от пользователя с id {}",
                eventId, userId);
        return service.getRequestsByUser(userId, eventId);
    }

    /**
     * Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto creatingDto) {

        log.info("POST-запрос на добавление нового события {} от пользователя {}", creatingDto, userId);
        return service.createEvent(userId, creatingDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventById(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @RequestBody @Valid UpdateEventUserRequest updatingDto) {

        log.info("PATCH-запрос на изменение события с id {} от текущего пользователя с id {} запроса: {}",
                eventId, userId, updatingDto);
        return service.updateEventById(userId, eventId, updatingDto);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventStatus(@PathVariable Long userId,
                                                            @PathVariable Long eventId,
                                                            @RequestBody(required = false) EventRequestStatusUpdateRequest updatingDto) {

        log.info("Изменение статуса запроса с id {} от текущего пользователя с id {}: {}", eventId, userId, updatingDto);
        return service.updateEventStatus(userId, eventId, updatingDto);
    }
}