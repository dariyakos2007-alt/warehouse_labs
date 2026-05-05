package com.example.warehouse.service;

import com.example.warehouse.dto.WarehouseDto;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.WarehouseMapper;
import com.example.warehouse.model.entity.Warehouse;
import com.example.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseService warehouseService;

    private Warehouse createWarehouse(Long id, String name) {
        Warehouse w = new Warehouse();
        w.setId(id);
        w.setName(name);
        return w;
    }

    private WarehouseDto createDto(Long id, String name) {
        WarehouseDto dto = new WarehouseDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    @Test
    void getAllWarehouses_shouldReturnList() {
        when(warehouseRepository.findAllWithStocks()).thenReturn(List.of(createWarehouse(1L, "WH1")));
        when(warehouseMapper.toDto(any())).thenReturn(createDto(1L, "WH1"));
        List<WarehouseDto> result = warehouseService.getAllWarehouses();
        assertEquals(1, result.size());
    }

    @Test
    void getWarehouseById_exists() {
        when(warehouseRepository.findByIdWithStocks(1L)).thenReturn(Optional.of(createWarehouse(1L, "WH1")));
        when(warehouseMapper.toDto(any())).thenReturn(createDto(1L, "WH1"));
        assertNotNull(warehouseService.getWarehouseById(1L));
    }

    @Test
    void getWarehouseById_notFound() {
        when(warehouseRepository.findByIdWithStocks(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> warehouseService.getWarehouseById(1L));
    }

    @Test
    void getWarehouseByName_exists() {
        when(warehouseRepository.findByNameWithStocks("WH1")).thenReturn(Optional.of(createWarehouse(1L, "WH1")));
        when(warehouseMapper.toDto(any())).thenReturn(createDto(1L, "WH1"));
        assertNotNull(warehouseService.getWarehouseByName("WH1"));
    }

    @Test
    void getWarehouseByName_notFound() {
        when(warehouseRepository.findByNameWithStocks("Absent")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> warehouseService.getWarehouseByName("Absent"));
    }

    @Test
    void getWarehouseWithStocks_exists() {
        when(warehouseRepository.findByIdWithStocks(1L)).thenReturn(Optional.of(createWarehouse(1L, "WH1")));
        when(warehouseMapper.toDto(any())).thenReturn(createDto(1L, "WH1"));
        assertNotNull(warehouseService.getWarehouseWithStocks(1L));
    }

    @Test
    void getWarehouseWithStocks_notFound() {
        when(warehouseRepository.findByIdWithStocks(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> warehouseService.getWarehouseWithStocks(1L));
    }

    @Test
    void createWarehouse_success() {
        WarehouseDto input = createDto(null, "NewWH");
        Warehouse entity = new Warehouse();
        Warehouse saved = createWarehouse(1L, "NewWH");
        WarehouseDto output = createDto(1L, "NewWH");
        when(warehouseMapper.toEntity(input)).thenReturn(entity);
        when(warehouseRepository.save(entity)).thenReturn(saved);
        when(warehouseMapper.toDto(saved)).thenReturn(output);

        WarehouseDto result = warehouseService.createWarehouse(input);

        assertEquals(1L, result.getId());
    }

    @Test
    void updateWarehouse_success() {
        Long id = 1L;
        Warehouse existing = createWarehouse(id, "Old");
        WarehouseDto input = createDto(null, "Updated");
        when(warehouseRepository.findById(id)).thenReturn(Optional.of(existing));
        when(warehouseRepository.save(existing)).thenReturn(existing);
        when(warehouseMapper.toDto(existing)).thenReturn(createDto(id, "Updated"));

        WarehouseDto result = warehouseService.updateWarehouse(id, input);

        assertEquals("Updated", result.getName());
    }

    @SuppressWarnings("squid:S5778")
    @Test
    void updateWarehouse_notFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> warehouseService.updateWarehouse(1L, new WarehouseDto()));
    }

    @SuppressWarnings("squid:S5778")
    @Test
    void deleteWarehouse_success() {
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        warehouseService.deleteWarehouse(1L);
        verify(warehouseRepository).deleteById(1L);
    }

    @Test
    void deleteWarehouse_notFound() {
        when(warehouseRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> warehouseService.deleteWarehouse(1L));
    }
}