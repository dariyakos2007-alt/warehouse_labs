package com.example.warehouse.service;

import com.example.warehouse.dto.StockDto;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.StockMapper;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.model.entity.Stock;
import com.example.warehouse.model.entity.Warehouse;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.repository.StockRepository;
import com.example.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private StockMapper stockMapper;

    @InjectMocks
    private StockService stockService;

    private Stock createStock(Long id, Integer quantity) {
        Stock stock = new Stock();
        stock.setId(id);
        stock.setQuantity(quantity);
        return stock;
    }

    private StockDto createStockDto(Long id, Integer quantity) {
        StockDto dto = new StockDto();
        dto.setId(id);
        dto.setQuantity(quantity);
        return dto;
    }

    @Test
    void getAllStocks_shouldReturnList() {
        Stock stock = createStock(1L, 10);
        StockDto dto = createStockDto(1L, 10);
        when(stockRepository.findAllWithDetails()).thenReturn(List.of(stock));
        when(stockMapper.toDto(stock)).thenReturn(dto);

        List<StockDto> result = stockService.getAllStocks();

        assertEquals(1, result.size());
    }

    @Test
    void getStockById_exists() {
        when(stockRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(createStock(1L, 10)));
        when(stockMapper.toDto(any())).thenReturn(createStockDto(1L, 10));
        assertNotNull(stockService.getStockById(1L));
    }

    @Test
    void getStockById_notFound() {
        when(stockRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stockService.getStockById(1L));
    }

    @Test
    void getStockWithDetails_success() {
        when(stockRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(createStock(1L, 10)));
        when(stockMapper.toDto(any())).thenReturn(createStockDto(1L, 10));
        assertNotNull(stockService.getStockWithDetails(1L));
    }

    @Test
    void getStockWithDetails_notFound() {
        when(stockRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stockService.getStockWithDetails(1L));
    }

    @Test
    void getAllStocksWithDetails_shouldReturnList() {
        when(stockRepository.findAllWithDetails()).thenReturn(List.of(createStock(1L, 10)));
        when(stockMapper.toDto(any())).thenReturn(createStockDto(1L, 10));
        assertEquals(1, stockService.getAllStocksWithDetails().size());
    }

    @Test
    void getStocksByProduct_shouldReturnList() {
        when(stockRepository.findByProductIdWithDetails(1L)).thenReturn(List.of(createStock(1L, 10)));
        when(stockMapper.toDto(any())).thenReturn(createStockDto(1L, 10));
        assertEquals(1, stockService.getStocksByProduct(1L).size());
    }

    @Test
    void getStocksByWarehouse_shouldReturnList() {
        when(stockRepository.findByWarehouseIdWithDetails(1L)).thenReturn(List.of(createStock(1L, 10)));
        when(stockMapper.toDto(any())).thenReturn(createStockDto(1L, 10));
        assertEquals(1, stockService.getStocksByWarehouse(1L).size());
    }

    @Test
    void getStockByProductAndWarehouse_exists() {
        when(stockRepository.findByProductIdAndWarehouseIdWithDetails(1L, 2L))
                .thenReturn(Optional.of(createStock(1L, 10)));
        when(stockMapper.toDto(any())).thenReturn(createStockDto(1L, 10));
        assertNotNull(stockService.getStockByProductAndWarehouse(1L, 2L));
    }

    @Test
    void getStockByProductAndWarehouse_notFound() {
        when(stockRepository.findByProductIdAndWarehouseIdWithDetails(1L, 2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stockService.getStockByProductAndWarehouse(1L, 2L));
    }

    @Test
    void getOverStock_shouldReturnList() {
        when(stockRepository.findOverStockWithDetails()).thenReturn(List.of(createStock(1L, 100)));
        when(stockMapper.toDto(any())).thenReturn(createStockDto(1L, 100));
        assertEquals(1, stockService.getOverStock().size());
    }

    @Test
    void createStock_success() {
        StockDto input = createStockDto(null, 20);
        input.setProductId(1L);
        input.setWarehouseId(2L);
        Product product = new Product();
        Warehouse warehouse = new Warehouse();
        Stock entity = new Stock();
        Stock saved = createStock(1L, 20);
        StockDto output = createStockDto(1L, 20);

        when(stockMapper.toEntity(input)).thenReturn(entity);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(stockRepository.save(entity)).thenReturn(saved);
        when(stockMapper.toDto(saved)).thenReturn(output);

        StockDto result = stockService.createStock(input);

        assertEquals(1L, result.getId());
        verify(stockRepository).save(entity);
    }

    @Test
    void createStock_productNotFound_throws() {
        StockDto input = createStockDto(null, 20);
        input.setProductId(1L);
        when(stockMapper.toEntity(input)).thenReturn(new Stock());
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stockService.createStock(input));
    }

    @Test
    void createStock_warehouseNotFound_throws() {
        StockDto input = createStockDto(null, 20);
        input.setProductId(1L);
        input.setWarehouseId(2L);
        when(stockMapper.toEntity(input)).thenReturn(new Stock());
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stockService.createStock(input));
    }

    @Test
    void updateStock_success() {
        Long id = 1L;
        Stock existing = createStock(id, 10);
        StockDto input = createStockDto(id, 30);
        when(stockRepository.findById(id)).thenReturn(Optional.of(existing));
        when(stockRepository.save(existing)).thenReturn(existing);
        when(stockMapper.toDto(existing)).thenReturn(input);

        StockDto result = stockService.updateStock(id, input);

        assertEquals(30, result.getQuantity());
    }

    @Test
    void updateStock_notFound() {
        when(stockRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stockService.updateStock(1L, createStockDto(1L, 10)));
    }

    @Test
    void deleteStock_success() {
        when(stockRepository.existsById(1L)).thenReturn(true);
        stockService.deleteStock(1L);
        verify(stockRepository).deleteById(1L);
    }

    @Test
    void deleteStock_notFound() {
        when(stockRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> stockService.deleteStock(1L));
    }

    @Test
    void addQuantity_success() {
        Stock stock = createStock(1L, 10);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(stock)).thenReturn(stock);
        when(stockMapper.toDto(stock)).thenReturn(createStockDto(1L, 15));

        StockDto result = stockService.addQuantity(1L, 5);

        assertEquals(15, result.getQuantity());
    }

    @Test
    void addQuantity_stockNotFound_throws() {
        when(stockRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stockService.addQuantity(1L, 5));
    }

    @Test
    void removeQuantity_success() {
        Stock stock = createStock(1L, 10);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(stock)).thenReturn(stock);
        when(stockMapper.toDto(stock)).thenReturn(createStockDto(1L, 5));

        StockDto result = stockService.removeQuantity(1L, 5);

        assertEquals(5, result.getQuantity());
    }

    @Test
    void removeQuantity_insufficient_throws() {
        Stock stock = createStock(1L, 3);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        assertThrows(IllegalArgumentException.class, () -> stockService.removeQuantity(1L, 5));
    }

    @Test
    void removeQuantity_stockNotFound_throws() {
        when(stockRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stockService.removeQuantity(1L, 5));
    }

    @Test
    void transferStock_success() {
        Stock fromStock = createStock(1L, 20);
        Stock toStock = createStock(2L, 5);
        when(stockRepository.findByProductIdAndWarehouseId(1L, 10L)).thenReturn(Optional.of(fromStock));
        when(stockRepository.findByProductIdAndWarehouseId(1L, 20L)).thenReturn(Optional.of(toStock));
        when(stockRepository.save(fromStock)).thenReturn(fromStock);
        when(stockRepository.save(toStock)).thenReturn(toStock);

        stockService.transferStock(1L, 10L, 20L, 5);

        assertEquals(15, fromStock.getQuantity());
        assertEquals(10, toStock.getQuantity());
        verify(stockRepository, times(2)).save(any());
    }

    @Test
    void transferStock_fromNotFound_throws() {
        when(stockRepository.findByProductIdAndWarehouseId(1L, 10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stockService.transferStock(1L, 10L, 20L, 5));
    }

    @Test
    void transferStock_toNotFound_createsNew() {
        Stock fromStock = createStock(1L, 20);
        when(stockRepository.findByProductIdAndWarehouseId(1L, 10L)).thenReturn(Optional.of(fromStock));
        when(stockRepository.findByProductIdAndWarehouseId(1L, 20L)).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(warehouseRepository.findById(20L)).thenReturn(Optional.of(new Warehouse()));
        when(stockRepository.save(any(Stock.class))).thenAnswer(inv -> inv.getArgument(0));

        stockService.transferStock(1L, 10L, 20L, 5);

        assertEquals(15, fromStock.getQuantity());
        verify(stockRepository, times(2)).save(any());
    }

    @Test
    void getAllStocksWithProblem_shouldReturnList() {
        when(stockRepository.findAllWithDetails()).thenReturn(List.of(createStock(1L, 10)));
        when(stockMapper.toDto(any())).thenReturn(createStockDto(1L, 10));
        assertEquals(1, stockService.getAllStocksWithProblem().size());
    }

    @Test
    void getAllStocksWithJoinFetch_shouldReturnList() {
        when(stockRepository.findAllWithWarehouseAndProduct()).thenReturn(List.of(createStock(1L, 10)));
        when(stockMapper.toDto(any())).thenReturn(createStockDto(1L, 10));
        assertEquals(1, stockService.getAllStocksWithJoinFetch().size());
    }
}