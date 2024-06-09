package ru.practicum.service;

import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsByParam(List<Long> users, List<String> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventShortDto> getPublicEventsByParam(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                               Integer from, Integer size, HttpServletRequest request);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updatingDto);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto getEventByIdAndUser(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUser(Long userId, Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto creatingDto);

    EventFullDto updateEventById(Long userId, Long eventId, UpdateEventUserRequest updatingDto);

    EventRequestStatusUpdateResult updateEventStatus(Long userId, Long eventId,
                                                     EventRequestStatusUpdateRequest updatingDto);

}
