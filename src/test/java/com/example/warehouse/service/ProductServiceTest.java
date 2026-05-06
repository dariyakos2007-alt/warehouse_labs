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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductCache productCache;

    @InjectMocks
    private ProductService productService;

    private ProductDto productDto;
    private Product product;

    @BeforeEach
    void setUp() {
        productService.init();

        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setPrice(100.0);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
    }

    @Test
    void getAllProducts_shouldReturnList() {
        when(productRepository.findAllWithStocksAndCategory()).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(List.of(productDto), result);
    }

    @Test
    void getProductById_whenExists_shouldReturn() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.getProductById(1L);

        assertSame(productDto, result);
    }

    @Test
    void getProductById_whenNotExists_shouldThrow() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getProductWithDetails_success() {
        when(productRepository.findByIdWithCategory(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        assertSame(productDto, productService.getProductWithDetails(1L));
    }

    @Test
    void getAllProductsWithDetails_shouldReturnList() {
        when(productRepository.findAllWithDetails()).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        assertEquals(1, productService.getAllProductsWithDetails().size());
    }

    @Test
    void createProduct_withExistingCategoryAndSuppliers_setsRelationsAndClearsCache() {
        ProductDto input = new ProductDto();
        input.setName("Product with relations");
        input.setPrice(200.0);
        input.setCategoryId(1L);
        input.setSupplierIds(Set.of(2L));

        Product entity = new Product();
        Category category = new Category();
        Supplier supplier = new Supplier();
        supplier.setId(2L);

        when(productMapper.toEntity(input)).thenReturn(entity);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier));
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toDto(entity)).thenReturn(productDto);

        ProductDto result = productService.createProduct(input);

        assertSame(productDto, result);
        assertSame(category, entity.getCategory());
        assertTrue(entity.getSuppliers().contains(supplier));
        verify(productCache).clearAll();
    }

    @Test
    void createProduct_withMissingCategory_shouldThrow() {
        ProductDto input = new ProductDto();
        input.setCategoryId(404L);

        when(productMapper.toEntity(input)).thenReturn(new Product());
        when(categoryRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(input));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_whenExists_shouldSaveAndClearCache() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.updateProduct(1L, productDto);

        assertSame(productDto, result);
        verify(productCache).clearAll();
    }

    @Test
    void updateProduct_whenNotExists_shouldThrow() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, productDto));
    }

    @Test
    void deleteProduct_whenExists_shouldDeleteAndClearCache() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
        verify(productCache).clearAll();
    }

    @Test
    void deleteProduct_whenNotExists_shouldThrow() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void searchProductsByName_shouldReturnList() {
        when(productRepository.findByNameContainingIgnoreCase("test")).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        assertEquals(1, productService.searchProductsByName("test").size());
    }

    @Test
    void getProductsByCategory_shouldReturnList() {
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        assertEquals(1, productService.getProductsByCategory(1L).size());
    }

    @Test
    void createProductWithStockNoTx_success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        Product savedProduct = new Product();
        savedProduct.setId(1L);

        when(productMapper.toEntity(productDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        productService.createProductWithStockNoTx(productDto, 1L, 5);

        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    void createProductWithStockNoTx_quantityInvalid_throws() {
        when(productMapper.toEntity(productDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));

        assertThrows(IllegalArgumentException.class,
                () -> productService.createProductWithStockNoTx(productDto, 1L, 0));
    }

    @Test
    void createProductWithStockTx_quantityNull_throws() {
        when(productMapper.toEntity(productDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));

        assertThrows(IllegalArgumentException.class,
                () -> productService.createProductWithStockTx(productDto, 1L, null));
    }

    @Test
    void getProductsByCategoryAndMaxPriceCached_cacheHit() {
        ProductSearchKey key = new ProductSearchKey("Electronics", 1000.0, 0, 10, "name");
        Page<ProductDto> cachedPage = new PageImpl<>(List.of(productDto));
        when(productCache.get(key)).thenReturn(cachedPage);

        Page<ProductDto> result = productService.getProductsByCategoryAndMaxPriceCached(
                "Electronics", 1000.0, 0, 10);

        assertSame(cachedPage, result);
        verify(productRepository, never()).findByCategoryAndMaxPriceWithFetch(any(), any(), any());
    }

    @Test
    void getProductsByCategoryAndMaxPriceCached_cacheMiss() {
        ProductSearchKey key = new ProductSearchKey("Electronics", 1000.0, 0, 10, "name");
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productCache.get(key)).thenReturn(null);
        when(productRepository.findByCategoryAndMaxPriceWithFetch(eq("Electronics"), eq(1000.0), any(Pageable.class)))
                .thenReturn(productPage);
        when(productMapper.toDto(product)).thenReturn(productDto);

        Page<ProductDto> result = productService.getProductsByCategoryAndMaxPriceCached(
                "Electronics", 1000.0, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productCache).put(eq(key), any(Page.class));
    }

    @Test
    void getProductsByCategoryAndMaxPriceCached_cacheMiss_empty() {
        ProductSearchKey key = new ProductSearchKey("Electronics", 1000.0, 0, 10, "name");
        when(productCache.get(key)).thenReturn(null);
        when(productRepository.findByCategoryAndMaxPriceWithFetch(eq("Electronics"), eq(1000.0), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ProductDto> result = productService.getProductsByCategoryAndMaxPriceCached(
                "Electronics", 1000.0, 0, 10);

        assertTrue(result.getContent().isEmpty());
        verify(productCache).put(eq(key), any(Page.class));
    }
}
