package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationsDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.Event;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.service.CompilationService;
import ru.practicum.storage.CompilationRepository;
import ru.practicum.storage.EventRepository;
import ru.practicum.utility.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationsDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        List<CompilationsDto> foundCompilations;

        if (pinned == null) {
            foundCompilations = compilationRepository.findAll(Pagination.withoutSort(from, size))
                    .stream()
                    .map(CompilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        } else {

            foundCompilations = compilationRepository.findAllByPinned(pinned, Pagination.withoutSort(from, size))
                    .stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
        }
        log.info("Найден список из {} подборок", foundCompilations.size());
        return foundCompilations;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationsDto getCompilationById(Long compId) {

        CompilationsDto foundCompilation = CompilationMapper.toCompilationDto(checkCompilation(compId));
        log.info("Найдена подборка с id {}", compId);
        return foundCompilation;
    }

    @Override
    public CompilationsDto createCompilation(NewCompilationDto creatingDto) {

        List<Event> eventList = eventRepository.findAllById(creatingDto.getEvents());
        Compilation savedComp = compilationRepository.save(CompilationMapper.toCompilation(creatingDto, eventList));
        log.info("Новая подборка сохранена с id {}", savedComp.getId());
        return CompilationMapper.toCompilationDto(savedComp);
    }

    @Override
    public void deleteCompilation(Long compId) {

        checkCompilation(compId);
        compilationRepository.deleteById(compId);
        log.info("Подборка с id {} удалена", compId);

    }

    @Override
    public CompilationsDto updateCompilation(Long compId, UpdateCompilationRequest updatingDto) {

        Compilation updatingComp = checkCompilation(compId);

        if (updatingDto.getPinned() != null) {
            updatingComp.setPinned(updatingDto.getPinned());
        }

        if (updatingDto.getTitle() != null) {
            updatingComp.setTitle(updatingDto.getTitle());
        }

        if (updatingDto.getEvents() != null) {
            List<Event> eventList = eventRepository.findAllByIdIn(updatingDto.getEvents());
            updatingComp.setEvents(eventList);
        }

        Compilation updatedComp = compilationRepository.save(updatingComp);
        log.info("Подборка с id {} обновлена: {}", compId, updatedComp.getEvents());

        return CompilationMapper.toCompilationDto(updatedComp);
    }

    private Compilation checkCompilation(Long compId) {

        return compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Подборка с id %d не существует", compId)));
    }
}
