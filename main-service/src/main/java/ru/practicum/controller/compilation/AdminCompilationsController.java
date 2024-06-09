package ru.practicum.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationsDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationsController {

    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationsDto createCompilation(@RequestBody @Valid NewCompilationDto creatingDto) {

        log.info("POST-запрос на создание новой подборки событий: {}", creatingDto);
        return service.createCompilation(creatingDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {

        log.info("DELETE-запрос на удаление подборки с id {}", compId);
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationsDto updateCompilation(@PathVariable Long compId,
                                             @RequestBody @Valid UpdateCompilationRequest updatingDto) {

        log.info("PATCH-запрос на обновление категории с id {}", compId);
        return service.updateCompilation(compId, updatingDto);
    }
}