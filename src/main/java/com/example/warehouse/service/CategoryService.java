package com.example.warehouse.service;

import com.example.warehouse.dto.CategoryDto;
import com.example.warehouse.mapper.CategoryMapper;
import com.example.warehouse.model.entity.Category;
import com.example.warehouse.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    public CategoryDto getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
        return categoryMapper.toDto(category);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {

        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new RuntimeException("Category with name " + categoryDto.getName() + " already exists");
        }

        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public CategoryDto getCategoryWithProducts(Long id) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    public List<CategoryDto> getAllCategoriesWithProducts() {
        return categoryRepository.findAllWithProducts().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}