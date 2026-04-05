package com.example.warehouse.controller;

import com.example.warehouse.dto.StockDto;
import com.example.warehouse.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Остатки товаров", description = "Методы для работы с остатками товаров на складах")
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "Демонстрация проблемы N+1", description = "Возвращает все остатки с проблемой N+1 запроса")
    @GetMapping("/demo/problem")
    public ResponseEntity<List<StockDto>> demoProblem() {
        return ResponseEntity.ok(stockService.getAllStocksWithProblem());
    }

    @Operation(summary = "Демонстрация решения N+1 (JOIN FETCH)", description = "Возвращает все остатки с использованием JOIN FETCH")
    @GetMapping("/demo/join-fetch")
    public ResponseEntity<List<StockDto>> demoJoinFetch() {
        return ResponseEntity.ok(stockService.getAllStocksWithJoinFetch());
    }

    @Operation(summary = "Получить все остатки", description = "Возвращает список всех остатков")
    @GetMapping
    public ResponseEntity<List<StockDto>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @Operation(summary = "Получить остаток по ID", description = "Возвращает остаток по его идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<StockDto> getStockById(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @Operation(summary = "Получить остаток с деталями", description = "Возвращает остаток с товаром и складом")
    @GetMapping("/{id}/with-details")
    public ResponseEntity<StockDto> getStockWithDetails(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockWithDetails(id));
    }

    @Operation(summary = "Получить все остатки с деталями", description = "Возвращает все остатки с товарами и складами")
    @GetMapping("/with-details")
    public ResponseEntity<List<StockDto>> getAllStocksWithDetails() {
        return ResponseEntity.ok(stockService.getAllStocksWithDetails());
    }

    @Operation(summary = "Получить остатки по товару", description = "Возвращает все остатки указанного товара")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockDto>> getStocksByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getStocksByProduct(productId));
    }

    @Operation(summary = "Получить остатки по складу", description = "Возвращает все остатки на указанном складе")
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StockDto>> getStocksByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockService.getStocksByWarehouse(warehouseId));
    }

    @Operation(summary = "Получить остаток по товару и складу", description = "Возвращает остаток для указанного товара на указанном складе")
    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<StockDto> getStockByProductAndWarehouse(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockService.getStockByProductAndWarehouse(productId, warehouseId));
    }

    @Operation(summary = "Получить пересклад", description = "Возвращает остатки, где количество превышает максимальное")
    @GetMapping("/over-stock")
    public ResponseEntity<List<StockDto>> getOverStock() {
        return ResponseEntity.ok(stockService.getOverStock());
    }

    @Operation(summary = "Создать остаток", description = "Создает новый остаток")
    @PostMapping
    public ResponseEntity<StockDto> createStock(@Valid @RequestBody StockDto stockDto) {
        return new ResponseEntity<>(stockService.createStock(stockDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить остаток", description = "Обновляет остаток по ID")
    @PutMapping("/{id}")
    public ResponseEntity<StockDto> updateStock(@PathVariable Long id, @Valid @RequestBody StockDto stockDto) {
        return ResponseEntity.ok(stockService.updateStock(id, stockDto));
    }

    @Operation(summary = "Удалить остаток", description = "Удаляет остаток по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Добавить количество", description = "Увеличивает количество товара на складе")
    @PatchMapping("/{id}/add")
    public ResponseEntity<StockDto> addQuantity(@PathVariable Long id, @Valid @RequestParam int amount) {
        return ResponseEntity.ok(stockService.addQuantity(id, amount));
    }

    @Operation(summary = "Удалить количество", description = "Уменьшает количество товара на складе")
    @PatchMapping("/{id}/remove")
    public ResponseEntity<StockDto> removeQuantity(@PathVariable Long id, @Valid @RequestParam int amount) {
        return ResponseEntity.ok(stockService.removeQuantity(id, amount));
    }

    @Operation(summary = "Переместить товар", description = "Перемещает товар между складами")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transferStock(
            @RequestParam Long productId,
            @RequestParam Long fromWarehouseId,
            @RequestParam Long toWarehouseId,
            @RequestParam int amount) {
        stockService.transferStock(productId, fromWarehouseId, toWarehouseId, amount);
        return ResponseEntity.ok().build();
    }
}