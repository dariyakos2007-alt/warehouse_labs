package com.example.warehouse.service;

import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.mapper.ProductMapper;
import com.example.warehouse.model.entity.Category;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.repository.CategoryRepository;
import com.example.warehouse.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAllWithStocks().stream()       // стало
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }

    public ProductDto getProductWithDetails(Long id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }

    public List<ProductDto> getAllProductsWithDetails() {
        return productRepository.findAllWithDetails().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + productDto.getCategoryId()));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + productDto.getCategoryId()));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<ProductDto> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
}