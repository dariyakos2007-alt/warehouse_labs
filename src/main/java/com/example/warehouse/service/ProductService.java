package com.example.warehouse.service;

import com.example.warehouse.cache.ProductCache;
import com.example.warehouse.cache.ProductSearchKey;
import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.ProductMapper;
import com.example.warehouse.model.entity.Category;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.model.entity.Stock;
import com.example.warehouse.model.entity.Supplier;
import com.example.warehouse.model.entity.Warehouse;
import com.example.warehouse.repository.CategoryRepository;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.repository.StockRepository;
import com.example.warehouse.repository.SupplierRepository;
import com.example.warehouse.repository.WarehouseRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final ProductCache productCache;

    private static final String NOT_FOUND_ID_MSG = "Product not found with id: ";
    private static final String CATEGORY_NOT_FOUND_MSG = "Category not found with id: ";
    private static final String SUPPLIER_NOT_FOUND_MSG = "Supplier not found with id: ";

    private ProductService self;

    @PostConstruct
    public void init() {
        this.self = this;
    }

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
        return productRepository.findAllWithStocksAndCategory().stream()
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

        productCache.clearAll();
        log.info("Cache cleared after creating product with id: {}", savedProduct.getId());

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

        productCache.clearAll();
        log.info("Cache cleared after updating product with id: {}", updatedProduct.getId());

        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
        }
        productRepository.deleteById(id);

        productCache.clearAll();
        log.info("Cache cleared after deleting product with id: {}", id);
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

    public void createProductWithStockNoTx(ProductDto productDto, Long warehouseId, Integer quantity) {
        Product product = productMapper.toEntity(productDto);
        setCategoryIfPresent(product, productDto.getCategoryId());
        setSuppliersIfPresent(product, productDto.getSupplierIds());

        Product savedProduct = productRepository.save(product);

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Склад не найден"));

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("количество должно быть положительным! Остаток НЕ сохранён");
        }

        Stock stock = new Stock(savedProduct, warehouse, quantity);
        stockRepository.save(stock);
    }

    @Transactional
    public void createProductWithStockTx(ProductDto productDto, Long warehouseId, Integer quantity) {
        Product product = productMapper.toEntity(productDto);
        setCategoryIfPresent(product, productDto.getCategoryId());
        setSuppliersIfPresent(product, productDto.getSupplierIds());

        Product savedProduct = productRepository.save(product);

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Склад не найден"));

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("количество должно быть положительным! Транзакция откатится");
        }

        Stock stock = new Stock(savedProduct, warehouse, quantity);
        stockRepository.save(stock);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategoryAndMaxPriceWithFetch(
            String categoryName, Double maxPrice, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.findByCategoryAndMaxPriceWithFetch(
                categoryName, maxPrice, pageable);

        return productPage.map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategoryAndMaxPriceNativeWithFetch(
            String categoryName, Double maxPrice, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.findByCategoryAndMaxPriceNativeWithFetch(
                categoryName, maxPrice, pageable);

        return productPage.map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategoryAndMaxPriceCached(
            String categoryName, Double maxPrice, int page, int size) {

        ProductSearchKey key = new ProductSearchKey(categoryName, maxPrice, page, size, "name");

        Page<ProductDto> cached = productCache.get(key);
        if (cached != null) {
            log.info("Cache HIT for key: category={}, maxPrice={}, page={}, size={}",
                    categoryName, maxPrice, page, size);
            return cached;
        }

        log.info("Cache MISS for key: category={}, maxPrice={}, page={}, size={}",
                categoryName, maxPrice, page, size);

        Page<ProductDto> result = self.getProductsByCategoryAndMaxPriceWithFetch(
                categoryName, maxPrice, page, size);

        productCache.put(key, result);
        log.info("Result cached for key: category={}, maxPrice={}, page={}, size={}",
                categoryName, maxPrice, page, size);

        return result;
    }
}