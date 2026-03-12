package com.example.warehouse.service;

import com.example.warehouse.dto.SupplierDto;
import com.example.warehouse.mapper.SupplierMapper;
import com.example.warehouse.model.entity.Supplier;
import com.example.warehouse.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toDto)
                .collect(Collectors.toList());
    }

    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierByName(String name) {
        Supplier supplier = supplierRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Supplier not found with name: " + name));
        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierByEmail(String email) {
        Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supplier not found with email: " + email));
        return supplierMapper.toDto(supplier);
    }

    @Transactional
    public SupplierDto createSupplier(SupplierDto supplierDto) {
        Supplier supplier = supplierMapper.toEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    @Transactional
    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        supplier.setName(supplierDto.getName());
        supplier.setContactPerson(supplierDto.getContactPerson());
        supplier.setPhone(supplierDto.getPhone());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setAddress(supplierDto.getAddress());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(updatedSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    public List<SupplierDto> searchSuppliersByName(String namePart) {
        return supplierRepository.findByNameContainingIgnoreCase(namePart).stream()
                .map(supplierMapper::toDto)
                .collect(Collectors.toList());
    }

    public SupplierDto getSupplierWithProducts(Long id) {
        Supplier supplier = supplierRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return supplierMapper.toDto(supplier);
    }
}