package com.example.warehouse.service;

import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.ProductMapper;
import com.example.warehouse.model.entity.Category;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.model.entity.Supplier;
import com.example.warehouse.repository.CategoryRepository;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;

    private static final String NOT_FOUND_ID_MSG = "Product not found with id: ";
    private static final String CATEGORY_NOT_FOUND_MSG = "Category not found with id: ";
    private static final String SUPPLIER_NOT_FOUND_MSG = "Supplier not found with id: ";

    private void setCategoryIfPresent(Product product, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MSG + categoryId));
            product.setCategory(category);
        }
    }

    private void setSuppliersIfPresent(Product product, Set<Long> supplierIds) {
        if (supplierIds != null && !supplierIds.isEmpty()) {
            Set<Supplier> suppliers = new HashSet<>();
            for (Long supplierId : supplierIds) {
                Supplier supplier = supplierRepository.findById(supplierId)
                        .orElseThrow(() -> new ResourceNotFoundException(SUPPLIER_NOT_FOUND_MSG + supplierId));
                suppliers.add(supplier);
            }
            product.setSuppliers(suppliers);
        }
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAllWithStocks().stream()
                .map(productMapper::toDto)
                .toList();
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        return productMapper.toDto(product);
    }

    public ProductDto getProductWithDetails(Long id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        return productMapper.toDto(product);
    }

    public List<ProductDto> getAllProductsWithDetails() {
        return productRepository.findAllWithDetails().stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        setCategoryIfPresent(product, productDto.getCategoryId());
        setSuppliersIfPresent(product, productDto.getSupplierIds());

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        setCategoryIfPresent(product, productDto.getCategoryId());
        setSuppliersIfPresent(product, productDto.getSupplierIds());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
        }
        productRepository.deleteById(id);
    }

    public List<ProductDto> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(productMapper::toDto)
                .toList();
    }

    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(productMapper::toDto)
                .toList();
    }
}