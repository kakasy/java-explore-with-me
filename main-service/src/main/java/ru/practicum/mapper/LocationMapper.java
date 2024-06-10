package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.entity.Location;

@UtilityClass
public class LocationMapper {

    public Location toLocation(LocationDto location) {

        return Location.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }
}
