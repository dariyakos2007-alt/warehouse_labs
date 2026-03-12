package com.example.warehouse.service;

import com.example.warehouse.dto.WarehouseDto;
import com.example.warehouse.mapper.WarehouseMapper;
import com.example.warehouse.model.entity.Warehouse;
import com.example.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public List<WarehouseDto> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toDto)
                .collect(Collectors.toList());
    }

    public WarehouseDto getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        return warehouseMapper.toDto(warehouse);
    }

    public WarehouseDto getWarehouseByName(String name) {
        Warehouse warehouse = warehouseRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with name: " + name));
        return warehouseMapper.toDto(warehouse);
    }

    @Transactional
    public WarehouseDto createWarehouse(WarehouseDto warehouseDto) {
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDto);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(savedWarehouse);
    }

    @Transactional
    public WarehouseDto updateWarehouse(Long id, WarehouseDto warehouseDto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));

        warehouse.setName(warehouseDto.getName());
        warehouse.setAddress(warehouseDto.getAddress());
        warehouse.setPhone(warehouseDto.getPhone());

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(updatedWarehouse);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new RuntimeException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }

    public WarehouseDto getWarehouseWithStocks(Long id) {
        Warehouse warehouse = warehouseRepository.findByIdWithStocks(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        return warehouseMapper.toDto(warehouse);
    }
}