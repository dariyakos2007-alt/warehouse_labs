package com.example.warehouse.controller;

import com.example.warehouse.dto.CategoryDto;
import com.example.warehouse.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Категории товаров", description = "Методы для работы с категориями товаров")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Получить все категории", description = "Возвращает список всех категорий")
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Получить категорию по ID", description = "Возвращает категорию по её идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Получить категорию по названию", description = "Возвращает категорию по её названию")
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }

    @Operation(summary = "Поиск категорий", description = "Поиск категорий по названию")
    @GetMapping("/search")
    public ResponseEntity<List<CategoryDto>> searchCategories(@Valid @RequestParam String name) {
        return ResponseEntity.ok(categoryService.searchCategoriesByName(name));
    }

    @Operation(summary = "Получить категорию с товарами", description = "Возвращает категорию со списком всех товаров в ней")
    @GetMapping("/{id}/with-products")
    public ResponseEntity<CategoryDto> getCategoryWithProducts(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryWithProducts(id));
    }

    @Operation(summary = "Получить все категории с товарами", description = "Возвращает все категории со списками товаров")
    @GetMapping("/with-products")
    public ResponseEntity<List<CategoryDto>> getAllCategoriesWithProducts() {
        return ResponseEntity.ok(categoryService.getAllCategoriesWithProducts());
    }

    @Operation(summary = "Создать категорию", description = "Создает новую категорию")
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить категорию", description = "Полностью обновляет категорию по ID")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
    }

    @Operation(summary = "Частично обновить категорию", description = "Частично обновляет категорию по ID")
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> patchCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto updated = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить категорию", description = "Удаляет категорию по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}