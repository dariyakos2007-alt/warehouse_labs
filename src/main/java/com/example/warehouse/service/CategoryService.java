package com.example.warehouse.service;

import com.example.warehouse.dto.CategoryDto;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.CategoryMapper;
import com.example.warehouse.model.entity.Category;
import com.example.warehouse.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private static final String NOT_FOUND_ID_MSG = "Category not found with id: ";
    private static final String NOT_FOUND_NAME_MSG = "Category not found with name: ";
    private static final String EXISTS_MSG = "Category with name ";

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAllWithProducts().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        return categoryMapper.toDto(category);
    }

    public CategoryDto getCategoryByName(String name) {
        Category category = categoryRepository.findByNameWithProducts(name)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_NAME_MSG + name));
        return categoryMapper.toDto(category);
    }

    public List<CategoryDto> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCaseWithProducts(name).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new IllegalArgumentException(EXISTS_MSG + categoryDto.getName() + " already exists");
        }
        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
        }
        categoryRepository.deleteById(id);
    }

    public CategoryDto getCategoryWithProducts(Long id) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        return categoryMapper.toDto(category);
    }

    public List<CategoryDto> getAllCategoriesWithProducts() {
        return categoryRepository.findAllWithProducts().stream()
                .map(categoryMapper::toDto)
                .toList();
    }
}
