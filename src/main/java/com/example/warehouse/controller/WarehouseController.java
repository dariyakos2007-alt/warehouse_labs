package com.example.warehouse.controller;

import com.example.warehouse.dto.WarehouseDto;
import com.example.warehouse.service.WarehouseService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Склады", description = "Методы для работы со складами")
@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "Получить все склады", description = "Возвращает список всех складов")
    @GetMapping
    public ResponseEntity<List<WarehouseDto>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @Operation(summary = "Получить склад по ID", description = "Возвращает склад по его идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDto> getWarehouseById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @Operation(summary = "Получить склад по названию", description = "Возвращает склад по его названию")
    @GetMapping("/name/{name}")
    public ResponseEntity<WarehouseDto> getWarehouseByName(@PathVariable String name) {
        return ResponseEntity.ok(warehouseService.getWarehouseByName(name));
    }

    @Operation(summary = "Получить склад с остатками", description = "Возвращает склад со списком всех остатков")
    @GetMapping("/{id}/with-stocks")
    public ResponseEntity<WarehouseDto> getWarehouseWithStocks(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseWithStocks(id));
    }

    @Operation(summary = "Создать склад", description = "Создает новый склад")
    @PostMapping
    public ResponseEntity<WarehouseDto> createWarehouse(@Valid @RequestBody WarehouseDto warehouseDto) {
        return new ResponseEntity<>(warehouseService.createWarehouse(warehouseDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить склад", description = "Полностью обновляет склад по ID")
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseDto> updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseDto warehouseDto) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDto));
    }

    @Operation(summary = "Частично обновить склад", description = "Частично обновляет склад по ID")
    @PatchMapping("/{id}")
    public ResponseEntity<WarehouseDto> patchWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseDto warehouseDto) {
        WarehouseDto updated = warehouseService.updateWarehouse(id, warehouseDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить склад", description = "Удаляет склад по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}