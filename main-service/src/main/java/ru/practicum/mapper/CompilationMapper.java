package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.compilation.CompilationsDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.Event;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public Compilation toCompilation(NewCompilationDto dto, List<Event> events) {

        return Compilation.builder()
                .title(dto.getTitle())
                .events(events)
                .pinned(dto.getPinned())
                .build();
    }

    public CompilationsDto toCompilationDto(Compilation compilation) {

        return CompilationsDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(compilation.getEvents()
                        .stream()
                        .map(e -> EventMapper.toEventShortDto(e, 0))
                        .collect(Collectors.toList()))
                .pinned(compilation.getPinned())
                .build();
    }
}
