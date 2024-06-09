package ru.practicum.service;

import ru.practicum.dto.compilation.CompilationsDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationsDto> getCompilations(Boolean pinned, Integer from, Integer size);

   CompilationsDto getCompilationById(Long compId);


    CompilationsDto createCompilation(NewCompilationDto creatingDto);

    void deleteCompilation(Long compId);

    CompilationsDto updateCompilation(Long compId, UpdateCompilationRequest updatingDto);
}
