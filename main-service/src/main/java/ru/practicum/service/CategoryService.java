package ru.practicum.service;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto creatingDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto updatingDto);

    List<CategoryDto> getCategoriesByParam(Integer from, Integer size);

    CategoryDto getCategoriesById(Long categoryId);
}
