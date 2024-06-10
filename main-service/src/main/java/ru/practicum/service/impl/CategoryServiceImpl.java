package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.entity.Category;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.service.CategoryService;
import ru.practicum.storage.CategoryRepository;
import ru.practicum.utility.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto creatingDto) {

        Category savedCategory = categoryRepository.save(CategoryMapper.toCategory(creatingDto));
        log.info("Новая категория сохранена с id {}", savedCategory.getId());

        return CategoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {

        checkCategory(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("Категория с id {} удалена", categoryId);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto updatingDto) {

        Category oldCat = checkCategory(categoryId);
        oldCat.setName(updatingDto.getName());
        Category updatedCat = categoryRepository.save(oldCat);
        log.info("Категория с id {} обновлена", categoryId);

        return CategoryMapper.toCategoryDto(updatedCat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesByParam(Integer from, Integer size) {

        List<Category> foundCategories = categoryRepository.findAll(Pagination.withoutSort(from, size)).toList();
        log.info("Найден список из {} категорий", foundCategories.size());

        return foundCategories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoriesById(Long categoryId) {

        CategoryDto foundCategory = CategoryMapper.toCategoryDto(checkCategory(categoryId));
        log.info("Найдена категория с id {}", categoryId);

        return foundCategory;
    }


    private Category checkCategory(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Категория с id %d не существует", catId)));
    }
}
