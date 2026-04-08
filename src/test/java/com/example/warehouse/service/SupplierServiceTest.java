package com.example.warehouse.service;

import com.example.warehouse.dto.SupplierDto;
import com.example.warehouse.exception.DemoTransactionException;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.SupplierMapper;
import com.example.warehouse.model.entity.Supplier;
import com.example.warehouse.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier createSupplier(Long id, String name, String phone, String email) {
        Supplier s = new Supplier();
        s.setId(id);
        s.setName(name);
        s.setPhone(phone);
        s.setEmail(email);
        return s;
    }

    private SupplierDto createDto(Long id, String name, String phone, String email) {
        SupplierDto dto = new SupplierDto();
        dto.setId(id);
        dto.setName(name);
        dto.setPhone(phone);
        dto.setEmail(email);
        return dto;
    }

    @Test
    void getAllSuppliers_shouldReturnList() {
        Supplier supplier = createSupplier(1L, "Sup", "123", "a@b.com");
        SupplierDto dto = createDto(1L, "Sup", "123", "a@b.com");
        when(supplierRepository.findAllWithProducts()).thenReturn(List.of(supplier));
        when(supplierMapper.toDto(supplier)).thenReturn(dto);

        List<SupplierDto> result = supplierService.getAllSuppliers();

        assertEquals(1, result.size());
    }

    @Test
    void getSupplierById_exists() {
        when(supplierRepository.findByIdWithProducts(1L)).thenReturn(Optional.of(createSupplier(1L, "Sup", "123", "a@b.com")));
        when(supplierMapper.toDto(any())).thenReturn(createDto(1L, "Sup", "123", "a@b.com"));
        assertNotNull(supplierService.getSupplierById(1L));
    }

    @Test
    void getSupplierById_notFound() {
        when(supplierRepository.findByIdWithProducts(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierById(1L));
    }

    @Test
    void getSupplierByName_exists() {
        when(supplierRepository.findByNameWithProducts("Sup")).thenReturn(Optional.of(createSupplier(1L, "Sup", "123", "a@b.com")));
        when(supplierMapper.toDto(any())).thenReturn(createDto(1L, "Sup", "123", "a@b.com"));
        assertNotNull(supplierService.getSupplierByName("Sup"));
    }

    @Test
    void getSupplierByName_notFound() {
        when(supplierRepository.findByNameWithProducts("Absent")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierByName("Absent"));
    }

    @Test
    void getSupplierByEmail_exists() {
        when(supplierRepository.findByEmailWithProducts("a@b.com")).thenReturn(Optional.of(createSupplier(1L, "Sup", "123", "a@b.com")));
        when(supplierMapper.toDto(any())).thenReturn(createDto(1L, "Sup", "123", "a@b.com"));
        assertNotNull(supplierService.getSupplierByEmail("a@b.com"));
    }

    @Test
    void getSupplierByEmail_notFound() {
        when(supplierRepository.findByEmailWithProducts("x@y.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierByEmail("x@y.com"));
    }

    @Test
    void getSupplierWithProducts_exists() {
        when(supplierRepository.findByIdWithProducts(1L)).thenReturn(Optional.of(createSupplier(1L, "Sup", "123", "a@b.com")));
        when(supplierMapper.toDto(any())).thenReturn(createDto(1L, "Sup", "123", "a@b.com"));
        assertNotNull(supplierService.getSupplierWithProducts(1L));
    }

    @Test
    void getSupplierWithProducts_notFound() {
        when(supplierRepository.findByIdWithProducts(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierWithProducts(1L));
    }

    @Test
    void searchSuppliersByName_shouldReturnList() {
        when(supplierRepository.findByNameContainingIgnoreCaseWithProducts("Sup")).thenReturn(List.of(createSupplier(1L, "Sup", "123", "a@b.com")));
        when(supplierMapper.toDto(any())).thenReturn(createDto(1L, "Sup", "123", "a@b.com"));
        assertEquals(1, supplierService.searchSuppliersByName("Sup").size());
    }

    @Test
    void createSupplier_success() {
        SupplierDto input = createDto(null, "NewSup", "+1234567890", "new@sup.com");
        Supplier entity = new Supplier();
        Supplier saved = createSupplier(1L, "NewSup", "+1234567890", "new@sup.com");
        SupplierDto output = createDto(1L, "NewSup", "+1234567890", "new@sup.com");

        when(supplierRepository.findByPhone(input.getPhone())).thenReturn(Optional.empty());
        when(supplierRepository.findByEmail(input.getEmail())).thenReturn(Optional.empty());
        when(supplierMapper.toEntity(input)).thenReturn(entity);
        when(supplierRepository.save(entity)).thenReturn(saved);
        when(supplierMapper.toDto(saved)).thenReturn(output);

        SupplierDto result = supplierService.createSupplier(input);

        assertEquals(1L, result.getId());
    }

    @Test
    void createSupplier_duplicatePhone_throws() {
        SupplierDto input = createDto(null, "Sup", "+123", "a@b.com");
        when(supplierRepository.findByPhone(input.getPhone())).thenReturn(Optional.of(new Supplier()));
        assertThrows(DataIntegrityViolationException.class, () -> supplierService.createSupplier(input));
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void createSupplier_duplicateEmail_throws() {
        SupplierDto input = createDto(null, "Sup", "+123", "a@b.com");
        when(supplierRepository.findByPhone(input.getPhone())).thenReturn(Optional.empty());
        when(supplierRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(new Supplier()));
        assertThrows(DataIntegrityViolationException.class, () -> supplierService.createSupplier(input));
    }

    @Test
    void createSupplierNotTransactional_throwsDemoException() {
        SupplierDto input = createDto(null, "Demo", "123", "demo@ex.com");
        Supplier entity = new Supplier();
        Supplier saved = createSupplier(1L, "Demo", "123", "demo@ex.com");
        when(supplierMapper.toEntity(input)).thenReturn(entity);
        when(supplierRepository.save(entity)).thenReturn(saved);
        assertThrows(DemoTransactionException.class, () -> supplierService.createSupplierNotTransactional(input));
        verify(supplierRepository).save(entity);
    }

    @Test
    void createSupplierTransactional_throwsDemoException() {
        SupplierDto input = createDto(null, "Demo", "123", "demo@ex.com");
        Supplier entity = new Supplier();
        Supplier saved = createSupplier(1L, "Demo", "123", "demo@ex.com");
        when(supplierMapper.toEntity(input)).thenReturn(entity);
        when(supplierRepository.save(entity)).thenReturn(saved);
        assertThrows(DemoTransactionException.class, () -> supplierService.createSupplierTransactional(input));
        verify(supplierRepository).save(entity);
    }

    @Test
    void updateSupplier_success() {
        Long id = 1L;
        Supplier existing = createSupplier(id, "Old", "111", "old@ex.com");
        SupplierDto input = createDto(null, "NewName", "222", "new@ex.com");
        when(supplierRepository.findById(id)).thenReturn(Optional.of(existing));
        when(supplierRepository.save(existing)).thenReturn(existing);
        when(supplierMapper.toDto(existing)).thenReturn(createDto(id, "NewName", "222", "new@ex.com"));

        SupplierDto result = supplierService.updateSupplier(id, input);

        assertEquals("NewName", result.getName());
        assertEquals("222", result.getPhone());
    }

    @Test
    void updateSupplier_notFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> supplierService.updateSupplier(1L, new SupplierDto()));
    }

    @Test
    void deleteSupplier_success() {
        when(supplierRepository.existsById(1L)).thenReturn(true);
        supplierService.deleteSupplier(1L);
        verify(supplierRepository).deleteById(1L);
    }

    @Test
    void deleteSupplier_notFound() {
        when(supplierRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> supplierService.deleteSupplier(1L));
    }
}