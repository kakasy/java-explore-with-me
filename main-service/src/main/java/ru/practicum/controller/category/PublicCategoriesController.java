package ru.practicum.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoriesController {

    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getCategoriesByParam(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос на получение списка  категорий: количество пропущенных - {}, элементов в наборе - {}",
                from, size);

        return service.getCategoriesByParam(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto getCompilationById(@PathVariable Long catId) {

        log.info("GET-запрос на получение подборки событий по id = {}", catId);

        return service.getCategoriesById(catId);
    }
}
