package ru.practicum.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationsDto;
import ru.practicum.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationsController {

    private final CompilationService service;

    @GetMapping
    List<CompilationsDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос на получение подборки событий: закрепленные - {}, " +
                        "количество пропущенных - {}, элементов в наборе - {}", pinned, from, size);

        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    CompilationsDto getCompilationById(@PathVariable Long compId) {

        log.info("GET-запрос на получение подборки событий по id = {}", compId);

        return service.getCompilationById(compId);
    }
}
