package com.example.warehouse.controller;

import com.example.warehouse.dto.WarehouseDto;
import com.example.warehouse.service.WarehouseService;
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
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<List<WarehouseDto>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDto> getWarehouseById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<WarehouseDto> getWarehouseByName(@PathVariable String name) {
        return ResponseEntity.ok(warehouseService.getWarehouseByName(name));
    }

    @GetMapping("/{id}/with-stocks")
    public ResponseEntity<WarehouseDto> getWarehouseWithStocks(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseWithStocks(id));
    }

    @PostMapping
    public ResponseEntity<WarehouseDto> createWarehouse(@RequestBody WarehouseDto warehouseDto) {
        return new ResponseEntity<>(warehouseService.createWarehouse(warehouseDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseDto> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDto warehouseDto) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WarehouseDto> patchWarehouse(@PathVariable Long id, @RequestBody WarehouseDto warehouseDto) {
        WarehouseDto updated = warehouseService.updateWarehouse(id, warehouseDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}