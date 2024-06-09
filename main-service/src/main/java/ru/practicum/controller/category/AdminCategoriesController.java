package ru.practicum.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoriesController {

    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto creatingDto) {

        log.info("POST-запрос на создание новой категории с уникальным именем: {}", creatingDto);
        return service.createCategory(creatingDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {

        log.info("DELETE-запрос на удаление категории с id {} которая не связана ни с одним событием", catId);
        service.deleteCategory(catId);
    }


    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody @Valid NewCategoryDto updatingDto) {

        log.info("PATCH-запрос на обновление категории с уникальным именем и id {}", catId);

        return service.updateCategory(catId, updatingDto);
    }
}
