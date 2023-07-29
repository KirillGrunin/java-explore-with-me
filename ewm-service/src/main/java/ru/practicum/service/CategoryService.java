package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    List<CategoryDto> getAllCategories(PageRequest page);

    CategoryDto getCategoryById(Long catId);
}