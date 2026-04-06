package com.example.warehouse.service;

import com.example.warehouse.dto.SupplierDto;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.SupplierMapper;
import com.example.warehouse.model.entity.Supplier;
import com.example.warehouse.repository.SupplierRepository;
import com.example.warehouse.exception.DemoTransactionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    private static final String NOT_FOUND_ID_MSG = "Поставщик не найден с id: ";

    public List<SupplierDto> getAllSuppliers() {
        log.debug("Поиск всех поставщиков");
        return supplierRepository.findAllWithProducts().stream()
                .map(supplierMapper::toDto)
                .toList();
    }

    public SupplierDto getSupplierById(Long id) {
        log.debug("Поиск поставщика по ID: {}", id);
        Supplier supplier = supplierRepository.findByIdWithProducts(id)
                .orElseThrow(() -> {
                    log.warn("Поставщик с ID {} не найден", id);
                    return new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
                });
        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierByName(String name) {
        log.debug("Поиск поставщика по названию: {}", name);
        Supplier supplier = supplierRepository.findByNameWithProducts(name)
                .orElseThrow(() -> {
                    log.warn("Поставщик с названием {} не найден", name);
                    return new ResourceNotFoundException("Поставщик не найден с именем: " + name);
                });
        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierByEmail(String email) {
        Supplier supplier = supplierRepository.findByEmailWithProducts(email)
                .orElseThrow(() -> new ResourceNotFoundException("Поставщик не найден с email: " + email));
        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierWithProducts(Long id) {
        Supplier supplier = supplierRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        return supplierMapper.toDto(supplier);
    }

    public List<SupplierDto> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCaseWithProducts(name).stream()
                .map(supplierMapper::toDto)
                .toList();
    }

    @Transactional
    public SupplierDto createSupplier(SupplierDto supplierDto) {

        if (supplierDto.getPhone() != null && !supplierDto.getPhone().isEmpty()) {
            if (supplierRepository.findByPhone(supplierDto.getPhone()).isPresent()) {
                throw new DataIntegrityViolationException("Телефон '" + supplierDto.getPhone() + "' уже используется другим поставщиком");
            }
        }

        if (supplierDto.getEmail() != null && !supplierDto.getEmail().isEmpty()) {
            if (supplierRepository.findByEmail(supplierDto.getEmail()).isPresent()) {
                throw new DataIntegrityViolationException("Email '" + supplierDto.getEmail() + "' уже используется другим поставщиком");
            }
        }

        Supplier supplier = supplierMapper.toEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Создан поставщик с ID: {}", savedSupplier.getId());
        return supplierMapper.toDto(savedSupplier);
    }

    private SupplierDto createSupplierInternal(SupplierDto supplierDto) {
        Supplier supplier = supplierMapper.toEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        throw new DemoTransactionException("ДЕМОНСТРАЦИЯ: Ошибка после сохранения поставщика! " + savedSupplier.getId());
    }

    public SupplierDto createSupplierNotTransactional(SupplierDto supplierDto) {
        return createSupplierInternal(supplierDto);
    }

    @Transactional
    public SupplierDto createSupplierTransactional(SupplierDto supplierDto) {
        return createSupplierInternal(supplierDto);
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
