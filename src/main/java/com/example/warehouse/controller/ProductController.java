package com.example.warehouse.controller;

import com.example.warehouse.dto.CreateProductWithStockRequest;
import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Товары", description = "Методы для работы с товарами")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Получить все товары", description = "Возвращает список всех товаров")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Получить товар по ID", description = "Возвращает товар по его идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Поиск товаров", description = "Поиск товаров по названию")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchProductsByName(query));
    }

    @Operation(summary = "Получить товар с деталями", description = "Возвращает товар с категорией, поставщиками и остатками")
    @GetMapping("/{id}/with-details")
    public ResponseEntity<ProductDto> getProductWithDetails(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductWithDetails(id));
    }

    @Operation(summary = "Получить все товары с деталями", description = "Возвращает все товары с категорией, поставщиками и остатками")
    @GetMapping("/with-details")
    public ResponseEntity<List<ProductDto>> getAllProductsWithDetails() {
        return ResponseEntity.ok(productService.getAllProductsWithDetails());
    }

    @Operation(summary = "Получить товары по категории", description = "Возвращает товары, принадлежащие указанной категории")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @Operation(summary = "Создать товар", description = "Создает новый товар")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить товар", description = "Полностью обновляет товар по ID")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @Operation(summary = "Частично обновить товар", description = "Частично обновляет товар по ID")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> patchProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        ProductDto updated = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить товар", description = "Удаляет товар по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Создать товар с остатком (без транзакции)", description = "Демонстрация работы без @Transactional")
    @PostMapping("/demo/create-with-stock/no-tx")
    public ResponseEntity<String> createProductWithStockNoTx(@Valid @RequestBody CreateProductWithStockRequest request) {
        try {
            productService.createProductWithStockNoTx(request.getProduct(), request.getWarehouseId(), request.getQuantity());
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @Operation(summary = "Создать товар с остатком (с транзакцией)", description = "Демонстрация работы с @Transactional")
    @PostMapping("/demo/create-with-stock/with-tx")
    public ResponseEntity<String> createProductWithStockTx(@Valid @RequestBody CreateProductWithStockRequest request) {
        try {
            productService.createProductWithStockTx(request.getProduct(), request.getWarehouseId(), request.getQuantity());
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @Operation(summary = "Поиск товаров с кэшем (JPQL)", description = "Поиск товаров по категории и цене с использованием кэша")
    @GetMapping("/by-category-cached")
    public ResponseEntity<Page<ProductDto>> getProductsByCategoryAndPriceCached(
            @RequestParam String categoryName,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductDto> products = productService.getProductsByCategoryAndMaxPriceCached(
                categoryName, maxPrice, page, size);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Поиск товаров с кэшем (Native)", description = "Поиск товаров по категории и цене с использованием кэша")
    @GetMapping("/by-category-native-cached")
    public ResponseEntity<Page<ProductDto>> getProductsByCategoryAndPriceNativeCached(
            @RequestParam String categoryName,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductDto> products = productService.getProductsByCategoryAndMaxPriceNativeCached(
                categoryName, maxPrice, page, size);
        return ResponseEntity.ok(products);
    }
}