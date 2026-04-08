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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static final String NOT_FOUND_ID_MSG = "Товар не найден с id: ";
    private static final String CATEGORY_NOT_FOUND_MSG = "Категория не найдена с id: ";
    private static final String SUPPLIER_NOT_FOUND_MSG = "Поставщик не найден с id: ";

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
        log.debug("Поиск всех товаров");
        return productRepository.findAllWithStocksAndCategory().stream()
                .map(productMapper::toDto)
                .toList();
    }

    public ProductDto getProductById(Long id) {
        log.debug("Поиск товара по ID: {}", id);
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> {
                    log.warn("Товар с ID {} не найден", id);
                    return new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
                });
        return productMapper.toDto(product);
    }

    public ProductDto getProductWithDetails(Long id) {
        log.debug("Поиск товара с деталями по ID: {}", id);
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> {
                    log.warn("Товар с деталями с ID {} не найден", id);
                    return new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
                });
        return productMapper.toDto(product);
    }

    public List<ProductDto> getAllProductsWithDetails() {
        return productRepository.findAllWithStocksAndCategory().stream()
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
        log.info("Кэш очищен после создания товара с id: {}", savedProduct.getId());

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
        log.info("Кэш очищен после обновления товар с id: {}", updatedProduct.getId());

        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
        }
        productRepository.deleteById(id);

        productCache.clearAll();
        log.info("Кэш очищен после удаления товаров с id: {}", id);
    }

    public List<ProductDto> searchProductsByName(String name) {
        log.debug("Поиск товаров по названию: {}", name);
        return productRepository.findByNameContainingIgnoreCaseWithDetails(name).stream()
                .map(productMapper::toDto)
                .toList();
    }

    public List<ProductDto> getProductsByCategory(Long categoryId) {
        log.debug("Поиск товаров по категории ID: {}", categoryId);
        return productRepository.findByCategoryIdWithDetails(categoryId).stream()
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
    public Page<ProductDto> getProductsByCategoryAndMaxPriceCached(
            String categoryName, Double maxPrice, int page, int size) {

        ProductSearchKey key = new ProductSearchKey(categoryName, maxPrice, page, size, "name");

        Page<ProductDto> cached = productCache.get(key);
        if (cached != null) {
            log.info("Cache HIT for key: {}", key);
            return cached;
        }

        log.info("Cache MISS for key: {}", key);

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.findByCategoryAndMaxPriceWithFetch(
                categoryName, maxPrice, pageable);

        Page<ProductDto> result = productPage.map(productMapper::toDto);
        productCache.put(key, result);
        log.info("Result cached for key: {}", key);

        return result;
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategoryAndMaxPriceNativeCached(
            String categoryName, Double maxPrice, int page, int size) {

        ProductSearchKey key = new ProductSearchKey(categoryName, maxPrice, page, size, "name");

        Page<ProductDto> cached = productCache.get(key);
        if (cached != null) {
            log.info("Cache HIT for key(native): {}", key);
            return cached;
        }

        log.info("Cache MISS for key(native): {}", key);

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Long> idsPage = productRepository.findProductIdsByCategoryAndMaxPriceNative(
                categoryName, maxPrice, pageable);

        if (idsPage.isEmpty()) {
            Page<ProductDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            productCache.put(key, emptyPage);
            return emptyPage;
        }

        List<Product> products = productRepository.findAllWithDetailsByIds(idsPage.getContent());

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<ProductDto> sortedProducts = idsPage.getContent().stream()
                .map(productMap::get)
                .map(productMapper::toDto)
                .toList();

        Page<ProductDto> result = new PageImpl<>(sortedProducts, pageable, idsPage.getTotalElements());

        productCache.put(key, result);
        log.info("Result cached for key(native): {}", key);

        return result;
    }

    @Transactional
    public List<ProductDto> createProductsBatchWithTx(List<ProductDto> productDtos) {
        log.debug("Создание {} товаров с транзакцией", productDtos.size());

        if (productDtos.isEmpty()) {
            throw new IllegalArgumentException("Список товаров не может быть пустым");
        }

        long distinctCount = productDtos.stream()
                .map(ProductDto::getName)
                .distinct()
                .count();
        if (distinctCount != productDtos.size()) {
            throw new IllegalArgumentException("Обнаружены дубликаты названий товаров");
        }

        for (ProductDto dto : productDtos) {
            checkNameUniqueness(dto.getName());
        }

        List<Product> products = productDtos.stream()
                .map(dto -> {
                    Product product = productMapper.toEntity(dto);
                    Optional.ofNullable(dto.getCategoryId())
                            .flatMap(categoryRepository::findById)
                            .ifPresent(product::setCategory);
                    return product;
                })
                .toList();

        List<Product> saved = productRepository.saveAll(products);
        return saved.stream()
                .map(productMapper::toDto)
                .toList();
    }

    private void checkNameUniqueness(String name) {
        Optional<Product> existing = productRepository.findByName(name);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Товар с названием '" + name + "' уже существует");
        }
    }

    public List<ProductDto> createProductsBatchWithoutTx(List<ProductDto> productDtos) {
        log.debug("Создание {} товаров БЕЗ транзакции", productDtos.size());

        if (productDtos.isEmpty()) {
            throw new IllegalArgumentException("Список товаров не может быть пустым");
        }

        List<ProductDto> responses = new ArrayList<>();
        for (ProductDto dto : productDtos) {
            if (productRepository.findByName(dto.getName()).isPresent()) {
                throw new IllegalArgumentException("Товар с названием '" + dto.getName() + "' уже существует");
            }
            Product product = productMapper.toEntity(dto);
            Optional.ofNullable(dto.getCategoryId())
                    .flatMap(categoryRepository::findById)
                    .ifPresent(product::setCategory);
            Product saved = productRepository.save(product);
            responses.add(productMapper.toDto(saved));
        }
        return responses;
    }
}
