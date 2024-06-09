package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.entity.User;
import ru.practicum.enums.EventStatus;

@UtilityClass
public class EventMapper {

    public EventShortDto toEventShortDto(Event event, Integer view) {

        return EventShortDto.builder()
                .id(event.getId())
                .paid(event.getPaid())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(UserShortDto.builder()
                        .id(event.getInitiator().getId())
                        .name(event.getInitiator().getName())
                        .build())
                .views(view)
                .build();
    }

    public EventFullDto toEventFullDto(Event event, Integer view) {

        return EventFullDto.builder()
                .id(event.getId())
                .paid(event.getPaid())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(UserShortDto.builder()
                        .id(event.getInitiator().getId())
                        .name(event.getInitiator().getName())
                        .build())
                .state(event.getState())
                .requestModeration(event.getRequestModeration())
                .views(view)
                .location(LocationDto.builder()
                        .lon(event.getLocation().getLon())
                        .lat(event.getLocation().getLat())
                        .build())
                .participantLimit(event.getParticipantLimit())
                .createdOn(event.getCreated())
                .publishedOn(event.getPublished())
                .build();
    }

    public Event toEvent(NewEventDto dto, Category category, User user) {

        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .initiator(user)
                .location(LocationMapper.toLocation(dto.getLocation()))
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .published(null)
                .requestModeration(dto.getRequestModeration())
                .state(EventStatus.PENDING)
                .title(dto.getTitle())
                .build();
    }
}
