package com.example.warehouse.controller;

import com.example.warehouse.dto.SupplierDto;
import com.example.warehouse.service.SupplierService;
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

@Tag(name = "Поставщики", description = "Методы для работы с поставщиками")
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "Получить всех поставщиков", description = "Возвращает список всех поставщиков")
    @GetMapping
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @Operation(summary = "Получить поставщика по ID", description = "Возвращает поставщика по его идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @Operation(summary = "Получить поставщика по названию", description = "Возвращает поставщика по его названию")
    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierDto> getSupplierByName(@PathVariable String name) {
        return ResponseEntity.ok(supplierService.getSupplierByName(name));
    }

    @Operation(summary = "Получить поставщика по email", description = "Возвращает поставщика по его email")
    @GetMapping("/email/{email}")
    public ResponseEntity<SupplierDto> getSupplierByEmail(@PathVariable String email) {
        return ResponseEntity.ok(supplierService.getSupplierByEmail(email));
    }

    @Operation(summary = "Получить поставщика с товарами", description = "Возвращает поставщика со списком его товаров")
    @GetMapping("/{id}/with-products")
    public ResponseEntity<SupplierDto> getSupplierWithProducts(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierWithProducts(id));
    }

    @Operation(summary = "Поиск поставщиков", description = "Поиск поставщиков по названию")
    @GetMapping("/search")
    public ResponseEntity<List<SupplierDto>> searchSuppliers(@Valid @RequestParam String name) {
        return ResponseEntity.ok(supplierService.searchSuppliersByName(name));
    }

    @Operation(summary = "Создать поставщика (без транзакции)", description = "Демонстрация работы без @Transactional")
    @PostMapping("/NotT")
    public ResponseEntity<SupplierDto> createSupplierNotTransactional(@Valid @RequestBody SupplierDto supplierDto) {
        SupplierDto result = supplierService.createSupplierNotTransactional(supplierDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Создать поставщика (с транзакцией)", description = "Демонстрация работы с @Transactional")
    @PostMapping("/T")
    public ResponseEntity<SupplierDto> createSupplierT(@Valid @RequestBody SupplierDto supplierDto) {
        SupplierDto result = supplierService.createSupplierTransactional(supplierDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Создать поставщика", description = "Создает нового поставщика")
    @PostMapping
    public ResponseEntity<SupplierDto> createSupplier(@Valid @RequestBody SupplierDto supplierDto) {
        return new ResponseEntity<>(supplierService.createSupplier(supplierDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить поставщика", description = "Полностью обновляет поставщика по ID")
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDto> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDto supplierDto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDto));
    }

    @Operation(summary = "Частично обновить поставщика", description = "Частично обновляет поставщика по ID")
    @PatchMapping("/{id}")
    public ResponseEntity<SupplierDto> patchSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDto supplierDto) {
        SupplierDto updated = supplierService.updateSupplier(id, supplierDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить поставщика", description = "Удаляет поставщика по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}