package com.example.warehouse.service;

import com.example.warehouse.dto.SupplierDto;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.SupplierMapper;
import com.example.warehouse.model.entity.Supplier;
import com.example.warehouse.repository.SupplierRepository;
import com.example.warehouse.exception.DemoTransactionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    private static final String NOT_FOUND_ID_MSG = "Supplier not found with id: ";

    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toDto)
                .toList();
    }

    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierByName(String name) {
        Supplier supplier = supplierRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with name: " + name));
        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierByEmail(String email) {
        Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with email: " + email));
        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierWithProducts(Long id) {
        Supplier supplier = supplierRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        return supplierMapper.toDto(supplier);
    }

    public List<SupplierDto> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name).stream()
                .map(supplierMapper::toDto)
                .toList();
    }

    @Transactional
    public SupplierDto createSupplier(SupplierDto supplierDto) {
        Supplier supplier = supplierMapper.toEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    public SupplierDto createSupplierNotTransactional(SupplierDto supplierDto) {
        Supplier supplier = supplierMapper.toEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);

        throw new DemoTransactionException("ДЕМОНСТРАЦИЯ: Ошибка после сохранения поставщика! " + savedSupplier.getId());
    }

    @Transactional
    public SupplierDto createSupplierTransactional(SupplierDto supplierDto) {
        Supplier supplier = supplierMapper.toEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);

        throw new DemoTransactionException("ДЕМОНСТРАЦИЯ: Ошибка после сохранения поставщика! " + savedSupplier.getId());
    }

    @Transactional
    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));

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
            throw new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
        }
        supplierRepository.deleteById(id);
    }
}