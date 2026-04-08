package com.example.warehouse.service;

import com.example.warehouse.dto.CategoryDto;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.CategoryMapper;
import com.example.warehouse.model.entity.Category;
import com.example.warehouse.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategories_shouldReturnList() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findAllWithProducts()).thenReturn(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        List<CategoryDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository).findAllWithProducts();
    }

    @Test
    void getCategoryById_whenExists_shouldReturnDto() {
        Long id = 1L;
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findByIdWithProducts(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto result = categoryService.getCategoryById(id);

        assertNotNull(result);
        verify(categoryRepository).findByIdWithProducts(id);
    }

    @Test
    void getCategoryById_whenNotExists_shouldThrowResourceNotFoundException() {
        Long id = 1L;
        when(categoryRepository.findByIdWithProducts(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(id));
    }

    @Test
    void getCategoryByName_whenExists_shouldReturnDto() {
        String name = "Electronics";
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findByNameWithProducts(name)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto result = categoryService.getCategoryByName(name);

        assertNotNull(result);
    }

    @Test
    void getCategoryByName_whenNotExists_shouldThrow() {
        String name = "Absent";
        when(categoryRepository.findByNameWithProducts(name)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryByName(name));
    }

    @Test
    void searchCategoriesByName_shouldReturnList() {
        String name = "Electro";
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findByNameContainingIgnoreCaseWithProducts(name)).thenReturn(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        List<CategoryDto> result = categoryService.searchCategoriesByName(name);

        assertEquals(1, result.size());
    }

    @Test
    void createCategory_whenNameUnique_shouldSaveAndReturnDto() {
        CategoryDto input = new CategoryDto();
        input.setName("NewCat");
        Category entity = new Category();
        Category saved = new Category();
        saved.setId(1L);
        CategoryDto output = new CategoryDto();
        output.setId(1L);

        when(categoryRepository.existsByName(input.getName())).thenReturn(false);
        when(categoryMapper.toEntity(input)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(saved);
        when(categoryMapper.toDto(saved)).thenReturn(output);

        CategoryDto result = categoryService.createCategory(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepository).save(entity);
    }

    @Test
    void createCategory_whenNameExists_shouldThrowDataIntegrityViolation() {
        CategoryDto input = new CategoryDto();
        input.setName("Existing");
        when(categoryRepository.existsByName(input.getName())).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> categoryService.createCategory(input));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_whenExists_shouldUpdateAndReturn() {
        Long id = 1L;
        CategoryDto input = new CategoryDto();
        input.setName("Updated");
        input.setDescription("Desc");
        Category existing = new Category();
        Category updated = new Category();
        CategoryDto output = new CategoryDto();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updated);
        when(categoryMapper.toDto(updated)).thenReturn(output);

        CategoryDto result = categoryService.updateCategory(id, input);

        assertNotNull(result);
        verify(categoryRepository).save(existing);
    }

    @Test
    void updateCategory_whenNotExists_shouldThrow() {
        Long id = 1L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(id, new CategoryDto()));
    }

    @Test
    void deleteCategory_whenExists_shouldDelete() {
        Long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(true);
        categoryService.deleteCategory(id);
        verify(categoryRepository).deleteById(id);
    }

    @Test
    void deleteCategory_whenNotExists_shouldThrow() {
        Long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(id));
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void getCategoryWithProducts_whenExists_shouldReturn() {
        Long id = 1L;
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findByIdWithProducts(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto result = categoryService.getCategoryWithProducts(id);

        assertNotNull(result);
    }

    @Test
    void getCategoryWithProducts_whenNotExists_shouldThrow() {
        Long id = 1L;
        when(categoryRepository.findByIdWithProducts(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryWithProducts(id));
    }

    @Test
    void getAllCategoriesWithProducts_shouldReturnList() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findAllWithProducts()).thenReturn(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        List<CategoryDto> result = categoryService.getAllCategoriesWithProducts();

        assertEquals(1, result.size());
    }
}