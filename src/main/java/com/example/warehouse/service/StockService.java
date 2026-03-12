package com.example.warehouse.service;

import com.example.warehouse.dto.StockDto;
import com.example.warehouse.mapper.StockMapper;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.model.entity.Stock;
import com.example.warehouse.model.entity.Warehouse;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.repository.StockRepository;
import com.example.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockMapper stockMapper;

    public List<StockDto> getAllStocks() {
        return stockRepository.findAll().stream().map(stockMapper::toDto).collect(Collectors.toList());
    }

    public StockDto getStockById(Long id) {
        Stock stock = stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found with id: " + id));
        return stockMapper.toDto(stock);
    }

    public StockDto getStockWithDetails(Long id) {
        Stock stock = stockRepository.findByIdWithDetails(id).orElseThrow(() -> new RuntimeException("Stock not found with id: " + id));
        return stockMapper.toDto(stock);
    }

    public List<StockDto> getAllStocksWithDetails() {
        return stockRepository.findAllWithDetails().stream().map(stockMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public StockDto createStock(StockDto stockDto) {
        Stock stock = stockMapper.toEntity(stockDto);

        if (stockDto.getProductId() != null) {
            Product product = productRepository.findById(stockDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + stockDto.getProductId()));
            stock.setProduct(product);
        }

        if (stockDto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(stockDto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + stockDto.getWarehouseId()));
            stock.setWarehouse(warehouse);
        }

        Stock savedStock = stockRepository.save(stock);
        return stockMapper.toDto(savedStock);
    }

    @Transactional
    public StockDto updateStock(Long id, StockDto stockDto) {
        Stock stock = stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found with id: " + id));

        stock.setQuantity(stockDto.getQuantity());
        stock.setMinQuantity(stockDto.getMinQuantity());
        stock.setMaxQuantity(stockDto.getMaxQuantity());

        Stock updatedStock = stockRepository.save(stock);
        return stockMapper.toDto(updatedStock);
    }

    @Transactional
    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new RuntimeException("Stock not found with id: " + id);
        }
        stockRepository.deleteById(id);
    }

    public List<StockDto> getStocksByProduct(Long productId) {
        return stockRepository.findByProductId(productId).stream().map(stockMapper::toDto).collect(Collectors.toList());
    }

    public List<StockDto> getStocksByWarehouse(Long warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId).stream().map(stockMapper::toDto).collect(Collectors.toList());
    }

    public StockDto getStockByProductAndWarehouse(Long productId, Long warehouseId) {
        Stock stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new RuntimeException("Stock not found for product " + productId + " and warehouse " + warehouseId));
        return stockMapper.toDto(stock);
    }

    public List<StockDto> getLowStock() {
        return stockRepository.findLowStock().stream().map(stockMapper::toDto).collect(Collectors.toList());
    }

    public List<StockDto> getOverStock() {
        return stockRepository.findOverStock().stream().map(stockMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StockDto addQuantity(Long id, int amount) {
        Stock stock = stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found with id: " + id));

        stock.addQuantity(amount);
        Stock updatedStock = stockRepository.save(stock);
        return stockMapper.toDto(updatedStock);
    }

    @Transactional
    public StockDto removeQuantity(Long id, int amount) {
        Stock stock = stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found with id: " + id));

        stock.removeQuantity(amount);
        Stock updatedStock = stockRepository.save(stock);
        return stockMapper.toDto(updatedStock);
    }

    @Transactional
    public void transferStock(Long productId, Long fromWarehouseId, Long toWarehouseId, int amount) {
        Stock fromStock = stockRepository.findByProductIdAndWarehouseId(productId, fromWarehouseId)
                .orElseThrow(() -> new RuntimeException("Stock not found in source warehouse"));

        Stock toStock = stockRepository.findByProductIdAndWarehouseId(productId, toWarehouseId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    Warehouse warehouse = warehouseRepository.findById(toWarehouseId)
                            .orElseThrow(() -> new RuntimeException("Warehouse not found"));
                    return new Stock(product, warehouse, 0);
                });

        fromStock.removeQuantity(amount);
        toStock.addQuantity(amount);

        stockRepository.save(fromStock);
        stockRepository.save(toStock);
    }
}