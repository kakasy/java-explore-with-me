package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.exception.InvalidArgumentException;
import ru.practicum.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Validated @RequestBody EndpointHitDto endpointHitDto) {

        log.info("POST-запрос '/hit' на сохранение информации: {} ", endpointHitDto);
        service.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(name = "uris", required = false) List<String> uriList,
            @RequestParam(name = "unique", defaultValue = "false") Boolean isUnique) {

        checkData(start, end);

        log.info(" GET-запрос '/stats' на статистику посещений в период с {} до {} по сервисам {} и уникальности {}",
                start, end, uriList, isUnique);
        return service.getStats(start, end, uriList, isUnique);
    }

    private void checkData(LocalDateTime start, LocalDateTime end) {

        if (start == null || end == null || end.isBefore(start)) {
            throw new InvalidArgumentException("Неверный временной диапазон");
        }
    }

}
