package com.example.warehouse.controller;

import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.service.ProductService;
import com.example.warehouse.dto.CreateProductWithStockRequest;
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
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchProductsByName(query));  // ← исправлено
    }

    @GetMapping("/{id}/with-details")
    public ResponseEntity<ProductDto> getProductWithDetails(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductWithDetails(id));
    }

    @GetMapping("/with-details")
    public ResponseEntity<List<ProductDto>> getAllProductsWithDetails() {
        return ResponseEntity.ok(productService.getAllProductsWithDetails());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> patchProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        ProductDto updated = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/demo/create-with-stock/no-tx")
    public ResponseEntity<String> createProductWithStockNoTx(@RequestBody CreateProductWithStockRequest request) {
        try {
            productService.createProductWithStockNoTx(request.getProduct(), request.getWarehouseId(), request.getQuantity());
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/demo/create-with-stock/with-tx")
    public ResponseEntity<String> createProductWithStockTx(@RequestBody CreateProductWithStockRequest request) {
        try {
            productService.createProductWithStockTx(request.getProduct(), request.getWarehouseId(), request.getQuantity());
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<ProductDto>> getProductsByCategoryAndPrice(
            @RequestParam String categoryName,
            @RequestParam Double maxPrice) {

        List<ProductDto> products = productService.getProductsByCategoryAndMaxPrice(categoryName, maxPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/by-category-paged")
    public ResponseEntity<Page<ProductDto>> getProductsByCategoryAndPricePaged(
            @RequestParam String categoryName,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductDto> products = productService.getProductsByCategoryAndMaxPricePaged(
                categoryName, maxPrice, page, size);
        return ResponseEntity.ok(products);
    }

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

    @GetMapping("/by-category-native")
    public ResponseEntity<List<ProductDto>> getProductsByCategoryAndPriceNative(
            @RequestParam String categoryName,
            @RequestParam Double maxPrice) {

        List<ProductDto> products = productService.getProductsByCategoryAndMaxPriceNative(categoryName, maxPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/by-category-native-paged")
    public ResponseEntity<Page<ProductDto>> getProductsByCategoryAndPriceNativePaged(
            @RequestParam String categoryName,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductDto> products = productService.getProductsByCategoryAndMaxPriceNativePaged(
                categoryName, maxPrice, page, size);
        return ResponseEntity.ok(products);
    }
}
