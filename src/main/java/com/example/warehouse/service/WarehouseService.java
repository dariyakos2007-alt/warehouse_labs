package com.example.warehouse.service;

import com.example.warehouse.dto.WarehouseDto;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.WarehouseMapper;
import com.example.warehouse.model.entity.Warehouse;
import com.example.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String NOT_FOUND_ID_MSG = "Warehouse not found with id: ";

    public List<WarehouseDto> getAllWarehouses() {
        return warehouseRepository.findAllWithStocks().stream()
                .map(warehouseMapper::toDto)
                .toList();
    }

    public WarehouseDto getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findByIdWithStocks(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
        return warehouseMapper.toDto(warehouse);
    }

    public WarehouseDto getWarehouseByName(String name) {
        Warehouse warehouse = warehouseRepository.findByNameWithStocks(name)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with name: " + name));
        return warehouseMapper.toDto(warehouse);
    }

    public WarehouseDto getWarehouseWithStocks(Long id) {
        Warehouse warehouse = warehouseRepository.findByIdWithStocks(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));
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
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ID_MSG + id));

        warehouse.setName(warehouseDto.getName());
        warehouse.setAddress(warehouseDto.getAddress());
        warehouse.setPhone(warehouseDto.getPhone());

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(updatedWarehouse);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND_ID_MSG + id);
        }
        warehouseRepository.deleteById(id);
    }
}
