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
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

        assertEquals(1, result.size());
        assertEquals(productDto, result.get(0));
    }

    @Test
    void getProductById_whenExists_shouldReturn() {
        when(productRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(productDto, result);
    }

    @Test
    void getProductById_whenNotExists_shouldThrow() {
        when(productRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getProductWithDetails_success() {
        when(productRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);
        assertNotNull(productService.getProductWithDetails(1L));
    }

    @Test
    void getProductWithDetails_notFound() {
        when(productRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductWithDetails(1L));
    }

    @Test
    void getAllProductsWithDetails_shouldReturnList() {
        when(productRepository.findAllWithStocksAndCategory()).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);
        assertEquals(1, productService.getAllProductsWithDetails().size());
    }

    @Test
    void createProduct_success() {
        ProductDto input = new ProductDto();
        input.setName("New");
        input.setPrice(50.0);
        Product entity = new Product();
        Product saved = new Product();
        saved.setId(2L);
        ProductDto output = new ProductDto();
        output.setId(2L);

        when(productMapper.toEntity(input)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(saved);
        when(productMapper.toDto(saved)).thenReturn(output);
        doNothing().when(productCache).clearAll();

        ProductDto result = productService.createProduct(input);

        assertEquals(2L, result.getId());
        verify(productCache).clearAll();
    }

    @Test
    void updateProduct_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);
        doNothing().when(productCache).clearAll();

        ProductDto result = productService.updateProduct(1L, productDto);

        assertNotNull(result);
        verify(productCache).clearAll();
    }

    @Test
    void updateProduct_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, productDto));
    }

    @Test
    void deleteProduct_success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
        verify(productCache).clearAll();
    }

    @Test
    void deleteProduct_notFound() {
        when(productRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void searchProductsByName_shouldReturnList() {
        when(productRepository.findByNameContainingIgnoreCaseWithDetails("test")).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);
        assertEquals(1, productService.searchProductsByName("test").size());
    }

    @Test
    void getProductsByCategory_shouldReturnList() {
        when(productRepository.findByCategoryIdWithDetails(1L)).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);
        assertEquals(1, productService.getProductsByCategory(1L).size());
    }

    @Test
    void createProductWithStockNoTx_success() {
        ProductDto dto = new ProductDto();
        dto.setName("Test");
        dto.setPrice(10.0);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        Product savedProduct = new Product();
        savedProduct.setId(1L);

        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(stockRepository.save(any(Stock.class))).thenReturn(new Stock());

        productService.createProductWithStockNoTx(dto, 1L, 5);

        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    void createProductWithStockNoTx_warehouseNotFound_throws() {
        when(productMapper.toEntity(any())).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> productService.createProductWithStockNoTx(productDto, 1L, 5));
    }

    @Test
    void createProductWithStockNoTx_quantityInvalid_throws() {
        when(productMapper.toEntity(any())).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));
        assertThrows(IllegalArgumentException.class, () -> productService.createProductWithStockNoTx(productDto, 1L, 0));
    }

    @Test
    void createProductWithStockTx_success() {
        when(productMapper.toEntity(any())).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));
        productService.createProductWithStockTx(productDto, 1L, 5);
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    void createProductWithStockTx_quantityInvalid_throws() {
        when(productMapper.toEntity(any())).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));
        assertThrows(IllegalArgumentException.class, () -> productService.createProductWithStockTx(productDto, 1L, 0));
    }

    @Test
    void getProductsByCategoryAndMaxPriceCached_cacheHit() {
        String category = "Electronics";
        Double maxPrice = 1000.0;
        ProductSearchKey key = new ProductSearchKey(category, maxPrice, 0, 10, "name");
        Page<ProductDto> cachedPage = new PageImpl<>(List.of(productDto));
        when(productCache.get(key)).thenReturn(cachedPage);

        Page<ProductDto> result = productService.getProductsByCategoryAndMaxPriceCached(category, maxPrice, 0, 10);

        assertSame(cachedPage, result);
        verify(productRepository, never()).findByCategoryAndMaxPriceWithFetch(any(), any(), any());
    }

    @Test
    void getProductsByCategoryAndMaxPriceCached_cacheMiss() {
        String category = "Electronics";
        Double maxPrice = 1000.0;
        ProductSearchKey key = new ProductSearchKey(category, maxPrice, 0, 10, "name");
        when(productCache.get(key)).thenReturn(null);
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findByCategoryAndMaxPriceWithFetch(eq(category), eq(maxPrice), any(Pageable.class)))
                .thenReturn(productPage);
        when(productMapper.toDto(product)).thenReturn(productDto);

        Page<ProductDto> result = productService.getProductsByCategoryAndMaxPriceCached(category, maxPrice, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productCache).put(eq(key), any(Page.class));
    }

    @Test
    void getProductsByCategoryAndMaxPriceNativeCached_cacheHit() {
        String category = "Electronics";
        Double maxPrice = 1000.0;
        ProductSearchKey key = new ProductSearchKey(category, maxPrice, 0, 10, "name");
        Page<ProductDto> cachedPage = new PageImpl<>(List.of(productDto));
        when(productCache.get(key)).thenReturn(cachedPage);

        Page<ProductDto> result = productService.getProductsByCategoryAndMaxPriceNativeCached(category, maxPrice, 0, 10);

        assertSame(cachedPage, result);
    }

    @Test
    void getProductsByCategoryAndMaxPriceNativeCached_cacheMiss_nonEmpty() {
        String category = "Electronics";
        Double maxPrice = 1000.0;
        ProductSearchKey key = new ProductSearchKey(category, maxPrice, 0, 10, "name");
        when(productCache.get(key)).thenReturn(null);
        Page<Long> idsPage = new PageImpl<>(List.of(1L));
        when(productRepository.findProductIdsByCategoryAndMaxPriceNative(eq(category), eq(maxPrice), any(Pageable.class)))
                .thenReturn(idsPage);
        when(productRepository.findAllWithDetailsByIds(List.of(1L))).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        Page<ProductDto> result = productService.getProductsByCategoryAndMaxPriceNativeCached(category, maxPrice, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productCache).put(eq(key), any(Page.class));
    }

    @Test
    void getProductsByCategoryAndMaxPriceNativeCached_cacheMiss_empty() {
        String category = "Electronics";
        Double maxPrice = 1000.0;
        ProductSearchKey key = new ProductSearchKey(category, maxPrice, 0, 10, "name");
        when(productCache.get(key)).thenReturn(null);
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.findProductIdsByCategoryAndMaxPriceNative(eq(category), eq(maxPrice), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<ProductDto> result = productService.getProductsByCategoryAndMaxPriceNativeCached(category, maxPrice, 0, 10);

        assertTrue(result.getContent().isEmpty());
        verify(productCache).put(eq(key), any(Page.class));
    }

    @Test
    void createProductsBatchWithTx_emptyList_throws() {
        assertThrows(IllegalArgumentException.class, () -> productService.createProductsBatchWithTx(Collections.emptyList()));
    }

    @Test
    void createProductsBatchWithTx_duplicateNames_throws() {
        List<ProductDto> list = List.of(createDto("A", 10.0), createDto("A", 20.0));
        assertThrows(IllegalArgumentException.class, () -> productService.createProductsBatchWithTx(list));
    }

    @Test
    void createProductsBatchWithTx_nameAlreadyExists_throws() {
        List<ProductDto> list = List.of(createDto("Existing", 10.0));
        when(productRepository.findByName("Existing")).thenReturn(Optional.of(new Product()));
        assertThrows(IllegalArgumentException.class, () -> productService.createProductsBatchWithTx(list));
    }

    @Test
    void createProductsBatchWithTx_success() {
        List<ProductDto> list = List.of(createDto("New1", 10.0), createDto("New2", 20.0));
        when(productRepository.findByName("New1")).thenReturn(Optional.empty());
        when(productRepository.findByName("New2")).thenReturn(Optional.empty());
        Product entity1 = new Product();
        Product entity2 = new Product();
        when(productMapper.toEntity(any(ProductDto.class))).thenReturn(entity1, entity2);
        when(productRepository.saveAll(anyList())).thenReturn(List.of(entity1, entity2));
        when(productMapper.toDto(entity1)).thenReturn(createDto("New1", 10.0));
        when(productMapper.toDto(entity2)).thenReturn(createDto("New2", 20.0));

        List<ProductDto> result = productService.createProductsBatchWithTx(list);

        assertEquals(2, result.size());
    }

    @Test
    void createProductsBatchWithoutTx_emptyList_throws() {
        assertThrows(IllegalArgumentException.class, () -> productService.createProductsBatchWithoutTx(Collections.emptyList()));
    }

    @Test
    void createProductsBatchWithoutTx_duplicateInLoop_throws() {
        ProductDto dto1 = createDto("Dup", 10.0);
        ProductDto dto2 = createDto("Dup", 20.0);
        List<ProductDto> input = Arrays.asList(dto1, dto2);

        Product entity1 = new Product();
        when(productMapper.toEntity(dto1)).thenReturn(entity1);
        when(productRepository.findByName("Dup"))
                .thenAnswer(invocation -> Optional.empty())
                .thenAnswer(invocation -> Optional.of(new Product()));
        when(productRepository.save(entity1)).thenReturn(entity1);

        assertThrows(IllegalArgumentException.class, () -> productService.createProductsBatchWithoutTx(input));

        verify(productRepository, times(1)).save(entity1);
    }

    @Test
    void createProductsBatchWithoutTx_success() {
        List<ProductDto> list = List.of(createDto("A", 10.0), createDto("B", 20.0));
        when(productRepository.findByName("A")).thenReturn(Optional.empty());
        when(productRepository.findByName("B")).thenReturn(Optional.empty());
        Product entity1 = new Product();
        Product entity2 = new Product();
        when(productMapper.toEntity(any(ProductDto.class))).thenReturn(entity1, entity2);
        when(productRepository.save(entity1)).thenReturn(entity1);
        when(productRepository.save(entity2)).thenReturn(entity2);
        when(productMapper.toDto(entity1)).thenReturn(createDto("A", 10.0));
        when(productMapper.toDto(entity2)).thenReturn(createDto("B", 20.0));

        List<ProductDto> result = productService.createProductsBatchWithoutTx(list);

        assertEquals(2, result.size());
    }

    private ProductDto createDto(String name, double price) {
        ProductDto dto = new ProductDto();
        dto.setName(name);
        dto.setPrice(price);
        return dto;
    }

    @Test
    void createProduct_withNonExistingCategory_throwsResourceNotFoundException() {
        ProductDto input = new ProductDto();
        input.setName("Test");
        input.setPrice(100.0);
        input.setCategoryId(999L);

        when(productMapper.toEntity(input)).thenReturn(new Product());
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(input));
    }

    @Test
    void createProduct_withValidSuppliers_setsSuppliers() {
        ProductDto input = new ProductDto();
        input.setName("Product with suppliers");
        input.setPrice(200.0);
        input.setSupplierIds(Set.of(1L, 2L));

        Product entity = new Product();
        Supplier supplier1 = new Supplier();
        supplier1.setId(1L);
        Supplier supplier2 = new Supplier();
        supplier2.setId(2L);

        when(productMapper.toEntity(input)).thenReturn(entity);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier1));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier2));
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toDto(entity)).thenReturn(new ProductDto());

        productService.createProduct(input);

        assertThat(entity.getSuppliers()).containsExactlyInAnyOrder(supplier1, supplier2);
        verify(productCache).clearAll();
    }

    @Test
    void createProduct_withNonExistingSupplier_throwsResourceNotFoundException() {
        ProductDto input = new ProductDto();
        input.setName("Test");
        input.setPrice(100.0);
        input.setSupplierIds(Set.of(1L, 2L));

        Product entity = new Product();
        when(productMapper.toEntity(input)).thenReturn(entity);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(new Supplier()));
        when(supplierRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(input));
    }

    @Test
    void createProduct_withExistingCategory_setsCategory() {
        ProductDto input = new ProductDto();
        input.setName("Test");
        input.setPrice(100.0);
        input.setCategoryId(1L);

        Product entity = new Product();
        Category category = new Category();
        category.setId(1L);

        when(productMapper.toEntity(input)).thenReturn(entity);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(entity);
        when(productMapper.toDto(entity)).thenReturn(new ProductDto());
        doNothing().when(productCache).clearAll();

        productService.createProduct(input);

        assertNotNull(entity.getCategory());
        assertEquals(category, entity.getCategory());
    }

    @Test
    void createProduct_withNonEmptySuppliers_entersIfBlock() {
        ProductDto input = new ProductDto();
        input.setName("Test");
        input.setPrice(100.0);
        input.setSupplierIds(Set.of(1L, 2L));

        Product entity = new Product();
        Supplier supplier1 = new Supplier();
        supplier1.setId(1L);
        Supplier supplier2 = new Supplier();
        supplier2.setId(2L);

        when(productMapper.toEntity(input)).thenReturn(entity);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier1));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier2));
        when(productRepository.save(any(Product.class))).thenReturn(entity);
        when(productMapper.toDto(entity)).thenReturn(new ProductDto());
        doNothing().when(productCache).clearAll();

        productService.createProduct(input);

        assertNotNull(entity.getSuppliers());
        assertEquals(2, entity.getSuppliers().size());
        assertTrue(entity.getSuppliers().contains(supplier1));
        assertTrue(entity.getSuppliers().contains(supplier2));
    }

    @Test
    void createProductWithStockNoTx_quantityNull_throws() {
        ProductDto emptyDto = new ProductDto();
        when(productMapper.toEntity(any())).thenReturn(new Product());
        when(productRepository.save(any())).thenReturn(new Product());
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));
        assertThrows(IllegalArgumentException.class, () -> productService.createProductWithStockNoTx(emptyDto, 1L, null));
    }

    @Test
    void createProductWithStockTx_quantityNull_throws() {
        ProductDto emptyDto = new ProductDto();
        when(productMapper.toEntity(any())).thenReturn(new Product());
        when(productRepository.save(any())).thenReturn(new Product());
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));
        assertThrows(IllegalArgumentException.class, () -> productService.createProductWithStockTx(emptyDto, 1L, null));
    }
}