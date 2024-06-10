package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.entity.Category;

@UtilityClass
public class CategoryMapper {

    public Category toCategory(NewCategoryDto dto) {

        return Category.builder()
                .name(dto.getName())
                .build();
    }

    public CategoryDto toCategoryDto(Category cat) {

        return CategoryDto.builder()
                .id(cat.getId())
                .name(cat.getName())
                .build();
    }
}
