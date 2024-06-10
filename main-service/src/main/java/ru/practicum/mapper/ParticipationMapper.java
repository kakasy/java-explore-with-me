package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.entity.Request;
import ru.practicum.enums.RequestStatus;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ParticipationMapper {

    public ParticipationRequestDto toRequestDto(Request request) {

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public static EventRequestStatusUpdateResult toRequestResult(List<Request> requests) {

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request req : requests) {
            if (req.getStatus().equals(RequestStatus.CONFIRMED)) {

                confirmedRequests.add(ParticipationMapper.toRequestDto(req));
            } else {
                rejectedRequests.add(ParticipationMapper.toRequestDto(req));
            }
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }
}
