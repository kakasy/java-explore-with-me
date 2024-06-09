package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.entity.Event;
import ru.practicum.entity.Request;
import ru.practicum.entity.User;
import ru.practicum.enums.EventStatus;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.ParticipationMapper;
import ru.practicum.service.RequestService;
import ru.practicum.storage.EventRepository;
import ru.practicum.storage.RequestRepository;
import ru.practicum.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {

        checkUser(userId);
        List<Request> requestList = requestRepository.findAllByRequesterId(userId);
        log.info("Найден список из {} запросов", requestList.size());

        return requestList.stream()
                .map(ParticipationMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

        if (!requestRepository.findAllByRequesterIdAndEventId(userId, eventId).isEmpty()) {

            throw new ConflictException("Запрос уже существует");
        }

        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        if (userId.equals(event.getInitiator().getId()) || !event.getState().equals(EventStatus.PUBLISHED)) {

            throw new ConflictException(String.format(
                    "Нельзя создать запрос на участие в событии %d от пользователя %d", eventId, userId));
        }

        log.info("Лимит участников: {}, уже записано: {}, премодерация {}",
                event.getParticipantLimit(), event.getConfirmedRequests(), event.getRequestModeration());

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {

            throw new ConflictException("Свободных мест нет");
        }

        Request request = new Request();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {

            request.setStatus(RequestStatus.CONFIRMED);
        } else {

            request.setStatus(RequestStatus.PENDING);
        }

        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        request.setEvent(event);

        Request savedRequest = requestRepository.save(request);

        log.info("Заявка создана {}", savedRequest);

        return ParticipationMapper.toRequestDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        checkUser(userId);
        Request request = checkRequest(requestId);
        request.setStatus(RequestStatus.CANCELED);
        Request updatedRequest = requestRepository.save(request);
        log.info("Заявка с id {} отменена", requestId);

        return ParticipationMapper.toRequestDto(updatedRequest);
    }

    private User checkUser(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Пользователь с id %d не существует", userId)));
    }

    private Event checkEvent(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id %d не существует", eventId)));
    }

    private Request checkRequest(Long requestId) {

        return requestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Запрос с id %d не существует", requestId)));
    }
}
