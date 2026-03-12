package com.example.warehouse.controller;

import com.example.warehouse.dto.SupplierDto;
import com.example.warehouse.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierDto> getSupplierByName(@PathVariable String name) {
        return ResponseEntity.ok(supplierService.getSupplierByName(name));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<SupplierDto> getSupplierByEmail(@PathVariable String email) {
        return ResponseEntity.ok(supplierService.getSupplierByEmail(email));
    }

    @GetMapping("/{id}/with-products")
    public ResponseEntity<SupplierDto> getSupplierWithProducts(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierWithProducts(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierDto>> searchSuppliers(@RequestParam String name) {
        return ResponseEntity.ok(supplierService.searchSuppliersByName(name));
    }

    @PostMapping
    public ResponseEntity<SupplierDto> createSupplier(@RequestBody SupplierDto supplierDto) {
        return new ResponseEntity<>(supplierService.createSupplier(supplierDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDto> updateSupplier(@PathVariable Long id, @RequestBody SupplierDto supplierDto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}