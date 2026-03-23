package com.example.warehouse.controller;

import com.example.warehouse.dto.StockDto;
import com.example.warehouse.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockDto>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDto> getStockById(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @GetMapping("/{id}/with-details")
    public ResponseEntity<StockDto> getStockWithDetails(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockWithDetails(id));
    }

    @GetMapping("/with-details")
    public ResponseEntity<List<StockDto>> getAllStocksWithDetails() {
        return ResponseEntity.ok(stockService.getAllStocksWithDetails());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockDto>> getStocksByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getStocksByProduct(productId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StockDto>> getStocksByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockService.getStocksByWarehouse(warehouseId));
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<StockDto> getStockByProductAndWarehouse(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockService.getStockByProductAndWarehouse(productId, warehouseId));
    }

    @GetMapping("/over-stock")
    public ResponseEntity<List<StockDto>> getOverStock() {
        return ResponseEntity.ok(stockService.getOverStock());
    }

    @PostMapping
    public ResponseEntity<StockDto> createStock(@RequestBody StockDto stockDto) {
        return new ResponseEntity<>(stockService.createStock(stockDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockDto> updateStock(@PathVariable Long id, @RequestBody StockDto stockDto) {
        return ResponseEntity.ok(stockService.updateStock(id, stockDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/add")
    public ResponseEntity<StockDto> addQuantity(@PathVariable Long id, @RequestParam int amount) {
        return ResponseEntity.ok(stockService.addQuantity(id, amount));
    }

    @PatchMapping("/{id}/remove")
    public ResponseEntity<StockDto> removeQuantity(@PathVariable Long id, @RequestParam int amount) {
        return ResponseEntity.ok(stockService.removeQuantity(id, amount));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferStock(
            @RequestParam Long productId,
            @RequestParam Long fromWarehouseId,
            @RequestParam Long toWarehouseId,
            @RequestParam int amount) {
        stockService.transferStock(productId, fromWarehouseId, toWarehouseId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/demo/no-tx")
    public ResponseEntity<?> demoNoTx() {
        try {
            stockService.demoWithoutTx();
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/demo/with-tx")
    public ResponseEntity<?> demoWithTx() {
        stockService.demoWithTx();
        return ResponseEntity.ok(Map.of(
                "status", "success"
        ));
    }

    @PostMapping("/demo/with-tx-error")
    public ResponseEntity<?> demoWithTxAndError() {
        try {
            stockService.demoWithTxAndError();
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}