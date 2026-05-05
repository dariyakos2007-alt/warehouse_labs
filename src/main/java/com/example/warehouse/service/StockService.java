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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockMapper stockMapper;


    private static final String NOT_FOUND_ID_MSG = "Остаток не найден с id: ";
    private static final String PRODUCT_NOT_FOUND_MSG = "Товар не найден с id: ";
    private static final String WAREHOUSE_NOT_FOUND_MSG = "Склад не найден с id: ";

    public List<StockDto> getAllStocks() {
        log.debug("Поиск всех остатков");
        return stockRepository.findAllWithDetails().stream()
                .map(stockMapper::toDto)
                .toList();
    }

    public StockDto getStockById(Long id) {
        log.debug("Поиск остатка по ID: {}", id);
        Stock stock = stockRepository.findByIdWithDetails(id)
                .orElseThrow(() ->  {
                    log.warn("Остаток с ID {} не найден", id);
                    return new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
                });
        return stockMapper.toDto(stock);
    }

    public StockDto getStockWithDetails(Long id) {
        Stock stock = stockRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        return stockMapper.toDto(stock);
    }

    public List<StockDto> getAllStocksWithDetails() {
        return stockRepository.findAllWithDetails().stream()
                .map(stockMapper::toDto)
                .toList();
    }

    public List<StockDto> getStocksByProduct(Long productId) {
        log.debug("Поиск остатков по товару ID: {}", productId);
        return stockRepository.findByProductIdWithDetails(productId).stream()
                .map(stockMapper::toDto)
                .toList();
    }

    public List<StockDto> getStocksByWarehouse(Long warehouseId) {
        log.debug("Поиск остатков по складу ID: {}", warehouseId);
        return stockRepository.findByWarehouseIdWithDetails(warehouseId).stream()
                .map(stockMapper::toDto)
                .toList();
    }

    public StockDto getStockByProductAndWarehouse(Long productId, Long warehouseId) {
        Stock stock = stockRepository.findByProductIdAndWarehouseIdWithDetails(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Остаток не найден для товара " + productId + " и склада " + warehouseId));
        return stockMapper.toDto(stock);
    }

    public List<StockDto> getOverStock() {
        return stockRepository.findOverStockWithDetails().stream()
                .map(stockMapper::toDto)
                .toList();
    }

    @Transactional
    public StockDto createStock(StockDto stockDto) {
        Stock stock = stockMapper.toEntity(stockDto);

        if (stockDto.getProductId() != null) {
            Product product = productRepository.findById(stockDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG + stockDto.getProductId()));
            stock.setProduct(product);
        }

        if (stockDto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(stockDto.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE_NOT_FOUND_MSG + stockDto.getWarehouseId()));
            stock.setWarehouse(warehouse);
        }

        Stock savedStock = stockRepository.save(stock);
        return stockMapper.toDto(savedStock);
    }

    @Transactional
    public StockDto updateStock(Long id, StockDto stockDto) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));

        stock.setQuantity(stockDto.getQuantity());
        stock.setMaxQuantity(stockDto.getMaxQuantity());

        Stock updatedStock = stockRepository.save(stock);
        return stockMapper.toDto(updatedStock);
    }

    @Transactional
    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
        }
        stockRepository.deleteById(id);
    }

    @Transactional
    public StockDto addQuantity(Long id, int amount) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        stock.addQuantity(amount);
        Stock updatedStock = stockRepository.save(stock);
        return stockMapper.toDto(updatedStock);
    }

    @Transactional
    public StockDto removeQuantity(Long id, int amount) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        stock.removeQuantity(amount);
        Stock updatedStock = stockRepository.save(stock);
        return stockMapper.toDto(updatedStock);
    }

    @Transactional
    public void transferStock(Long productId, Long fromWarehouseId, Long toWarehouseId, int amount) {
        Stock fromStock = stockRepository.findByProductIdAndWarehouseId(productId, fromWarehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Остаток не найден в основном складе"));

        Stock toStock = stockRepository.findByProductIdAndWarehouseId(productId, toWarehouseId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG + productId));
                    Warehouse warehouse = warehouseRepository.findById(toWarehouseId)
                            .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE_NOT_FOUND_MSG + toWarehouseId));
                    return new Stock(product, warehouse, 0);
                });

        fromStock.removeQuantity(amount);
        toStock.addQuantity(amount);

        stockRepository.save(fromStock);
        stockRepository.save(toStock);
    }

    public List<StockDto> getAllStocksWithProblem() {
        return stockRepository.findAllWithDetails().stream()
                .map(stockMapper::toDto)
                .toList();
    }

    public List<StockDto> getAllStocksWithJoinFetch() {
        return stockRepository.findAllWithWarehouseAndProduct().stream()
                .map(stockMapper::toDto)
                .toList();
    }

}
