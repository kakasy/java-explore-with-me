package ru.practicum.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.entity.Request;
import ru.practicum.entity.User;
import ru.practicum.enums.EventStatus;
import ru.practicum.enums.RequestStatus;
import ru.practicum.enums.StateAction;
import ru.practicum.enums.UpdateStateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.mapper.ParticipationMapper;
import ru.practicum.service.EventService;
import ru.practicum.spec.EventSpec;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.storage.CategoryRepository;
import ru.practicum.storage.EventRepository;
import ru.practicum.storage.RequestRepository;
import ru.practicum.storage.UserRepository;
import ru.practicum.utility.Pagination;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    public static final Integer EVENT_START_LAG_HOUR = 2;
    public static final Integer EVENT_UPDATE_LAG_HOUR = 1;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByParam(List<Long> users, List<String> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Integer from, Integer size) {

        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {

            throw new ValidationException("Окончание события должны быть позже его начала");
        }


        Specification<Event> specification = Specification
                .where(EventSpec.initiatorsIn(users))
                .and(EventSpec.categoriesIn(categories))
                .and(EventSpec.stateIn(states))
                .and(EventSpec.dateTimeAfter(rangeStart))
                .and(EventSpec.dateTimeBefore(rangeEnd));

        List<Event> foundEvents = eventRepository.findAll(specification, Pagination.withoutSort(from, size)).toList();
        log.info("Найдено {} событий", foundEvents.size());

        Map<Long, Integer> views = getViews(foundEvents);

        return foundEvents.stream()
                .map(e -> EventMapper.toEventFullDto(e, views.getOrDefault(e.getId(), 0)))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEventsByParam(String text, List<Long> categories, Boolean paid,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Boolean onlyAvailable, Integer from, Integer size,
                                                      HttpServletRequest request) {

        Specification<Event> specification = Specification
                .where(EventSpec.containText(text))
                .and(EventSpec.categoriesIn(categories))
                .and(EventSpec.isPaid(paid))
                .and(EventSpec.stateIn(List.of("PUBLISHED")))
                .and(EventSpec.dateTimeAfterOrNow(rangeStart))
                .and(EventSpec.dateTimeBeforeOrNow(rangeEnd));
        List<Event> foundEvents = eventRepository.findAll(specification, Pagination.withoutSort(from, size)).toList();

        if (onlyAvailable) foundEvents.stream()
                .filter(e -> e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit())
                .collect(Collectors.toList());

        log.info("Найдено {} событий", foundEvents.size());

        addStatistic(request);

        Map<Long, Integer> views = getViews(foundEvents);

        return foundEvents.stream()
                .map(e -> EventMapper.toEventShortDto(e, views.getOrDefault(e.getId(), 0)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updatingDto) {

        Event oldEvent = checkEvent(eventId);

        if (oldEvent.getState().equals(EventStatus.PUBLISHED) || oldEvent.getState().equals(EventStatus.CANCELED)) {

            throw new ConflictException(String.format("Событие уже %s", oldEvent.getState()));
        }

        if (oldEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(EVENT_UPDATE_LAG_HOUR))) {

            throw new ConflictException("Нарушены условия редактирования");
        }

        oldEvent = fillEventForUpdating(oldEvent, updatingDto.getAnnotation(), updatingDto.getCategory(),
                updatingDto.getDescription(), updatingDto.getEventDate(), updatingDto.getLocation(),
                updatingDto.getPaid(), updatingDto.getParticipantLimit(), updatingDto.getRequestModeration(),
                updatingDto.getTitle());

        if (updatingDto.getStateAction() != null
                && updatingDto.getStateAction().equals(UpdateStateAction.REJECT_EVENT)
                && oldEvent.getState().equals(EventStatus.PENDING)) {

            oldEvent.setState(EventStatus.CANCELED);
        }

        if (updatingDto.getStateAction() != null
                && updatingDto.getStateAction().equals(UpdateStateAction.PUBLISH_EVENT)
                && oldEvent.getState().equals(EventStatus.PENDING)) {

            oldEvent.setState(EventStatus.PUBLISHED);
            oldEvent.setPublished(LocalDateTime.now());
        }

        Event newEvent = eventRepository.save(oldEvent);
        log.info("Обновлено событие: {}", newEvent);

        Map<Long, Integer> views = getViews(List.of(newEvent));

        return EventMapper.toEventFullDto(newEvent, views.getOrDefault(newEvent.getId(), 0));
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {

        Event event = eventRepository.findByIdAndState(eventId, EventStatus.PUBLISHED).orElseThrow(() ->
                new EntityNotFoundException(String.format("Событие с id %d не существует или недоступно", eventId)));

        log.info("Найдено событие: {}", event);

        addStatistic(request);

        Map<Long, Integer> views = getViews(List.of(event));

        return EventMapper.toEventFullDto(event, views.getOrDefault(event.getId(), 0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {

        List<Event> eventsList = eventRepository.findEventByInitiatorId(userId, Pagination.withoutSort(from, size));
        log.info("Найдено {} событий от пользователя {}", eventsList.size(), userId);

        Map<Long, Integer> views = getViews(eventsList);

        return eventsList.stream()
                .map(e -> EventMapper.toEventShortDto(e, views.getOrDefault(e.getId(), 0)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdAndUser(Long userId, Long eventId) {

        Event foundEvent = checkEventAndInitiator(eventId, userId);
        log.info("Найдено событие {} от пользователя {}", eventId, userId);

        Map<Long, Integer> views = getViews(List.of(foundEvent));

        return EventMapper.toEventFullDto(foundEvent, views.getOrDefault(foundEvent.getId(), 0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUser(Long userId, Long eventId) {

        Event event = checkEvent(eventId);
        if (!Objects.equals(userId, event.getInitiator().getId())) {
            return new ArrayList<>();
        }
        List<Request> requestList = requestRepository.findAllByEventId(eventId);
        log.info("Найдено {} запросов на участие в событии {}", requestList.size(), eventId);
        return requestList.stream().map(ParticipationMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto creatingDto) {

        if (creatingDto.getEventDate().isBefore(LocalDateTime.now().plusHours(EVENT_START_LAG_HOUR))) {
            throw new ConflictException("Событие начинается слишком рано");
        }

        User user = checkUser(userId);
        Category category = checkCategory(creatingDto.getCategory());
        Event savedEvent = eventRepository.save(EventMapper.toEvent(creatingDto, category, user));
        log.info("Событие добавлено {}", savedEvent);

        return EventMapper.toEventFullDto(savedEvent, 0);
    }

    @Override
    public EventFullDto updateEventById(Long userId, Long eventId, UpdateEventUserRequest updatingDto) {

        Event oldEvent = checkEventAndInitiator(eventId, userId);

        if (EventStatus.PUBLISHED.equals(oldEvent.getState())
                || oldEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(EVENT_START_LAG_HOUR))) {

            throw new ConflictException("Нарушены условия редактирования");
        }

        oldEvent = fillEventForUpdating(oldEvent, updatingDto.getAnnotation(), updatingDto.getCategory(),
                updatingDto.getDescription(), updatingDto.getEventDate(), updatingDto.getLocation(),
                updatingDto.getPaid(), updatingDto.getParticipantLimit(), updatingDto.getRequestModeration(),
                updatingDto.getTitle());

        if (updatingDto.getStateAction() != null
                && updatingDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            oldEvent.setState(EventStatus.CANCELED);
        }
        if (updatingDto.getStateAction() != null
                && updatingDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            oldEvent.setState(EventStatus.PENDING);
        }
        Event newEvent = eventRepository.save(oldEvent);
        log.info("Событие обновлено {}", newEvent);

        Map<Long, Integer> views = getViews(List.of(newEvent));

        return EventMapper.toEventFullDto(newEvent, views.getOrDefault(newEvent.getId(), 0));
    }

    @Override
    public EventRequestStatusUpdateResult updateEventStatus(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest updatingDto) {

        Event event = checkEventAndInitiator(eventId, userId);
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {

            throw new ConflictException("Нет свободных мест");
        }

        List<Request> requestsList = requestRepository.findAllById(updatingDto.getRequestIds());
        for (Request req : requestsList) {
            if (!req.getStatus().equals(RequestStatus.PENDING)) {

                throw new ConflictException(String.format("Запрос %d уже имеет статус", req.getId()));
            }

            if (event.getConfirmedRequests() < event.getParticipantLimit()
                    && updatingDto.getStatus().equals(RequestStatus.CONFIRMED)) {

                req.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else {

                req.setStatus(RequestStatus.REJECTED);
            }
        }
        requestRepository.saveAll(requestsList);
        log.info("Статус заявок изменён");
        return ParticipationMapper.toRequestResult(requestsList);
    }

    private Map<Long, Integer> getViews(List<Event> events) {

        if (events.isEmpty()) {

            return Map.of();
        }
        String[] uris = events.stream()
                .map(e -> String.format("/events/%d", e.getId())).toArray(String[]::new);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = LocalDateTime.now().minusHours(24).format(format);
        String end = LocalDateTime.now().plusMinutes(1).format(format);

        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, true);
        List<ViewStatsDto> viewStatsList = mapper.convertValue(response.getBody(), new TypeReference<>() {
        });

        Map<Long, Integer> result = new HashMap<>();
        for (ViewStatsDto view : viewStatsList) {
            String index = view.getUri().substring(8);
            result.put(Long.parseLong(index), Math.toIntExact(view.getHits()));
        }
        return result;
    }

    private void addStatistic(HttpServletRequest request) {

        EndpointHitDto hit = EndpointHitDto.builder()
                .app("main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        log.info("Отправлена информация о посещении: client ip: {}, endpoint path: {}", hit.getIp(), hit.getUri());
        statsClient.addHit(hit);
    }

    private Event checkEvent(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id %d не существует", eventId)));
    }

    private User checkUser(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Пользователь с id %d не существует", userId)));
    }

    private Category checkCategory(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Категория с id %d не существует", catId)));
    }

    private Event checkEventAndInitiator(Long eventId, Long userId) {

        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("События с id %d от пользователя %d не существует", eventId, userId)));
    }


    private Event fillEventForUpdating(Event event, String annotation, Long category, String description,
                                       LocalDateTime eventDate, LocationDto location, Boolean paid,
                                       Integer participantLimit, Boolean requestModeration, String title) {

        if (annotation != null) {
            event.setAnnotation(annotation);
        }

        if (category != null) {
            event.setCategory(checkCategory(category));
        }

        if (description != null) {
            event.setDescription(description);
        }

        if (eventDate != null) {
            event.setEventDate(eventDate);
        }

        if (location != null) {
            event.setLocation(LocationMapper.toLocation(location));
        }

        if (paid != null) {
            event.setPaid(paid);
        }

        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }

        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }

        if (title != null) {
            event.setTitle(title);
        }
        return event;
    }
}
