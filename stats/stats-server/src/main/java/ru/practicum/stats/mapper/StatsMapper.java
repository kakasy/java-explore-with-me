package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.entity.EndpointHit;

@UtilityClass
public class StatsMapper {

    public EndpointHit toEndpointHit(EndpointHitDto endpointHitRequest) {

        return EndpointHit.builder()
                .app(endpointHitRequest.getApp())
                .ip(endpointHitRequest.getIp())
                .uri(endpointHitRequest.getUri())
                .times(endpointHitRequest.getTimestamp())
                .build();
    }
}
