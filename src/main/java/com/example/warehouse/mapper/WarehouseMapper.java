package com.example.warehouse.mapper;

import com.example.warehouse.dto.WarehouseDto;
import com.example.warehouse.model.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseDto toDto(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }

        WarehouseDto dto = new WarehouseDto();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setAddress(warehouse.getAddress());
        dto.setPhone(warehouse.getPhone());
        dto.setTotalProducts(warehouse.getStocks() != null ? warehouse.getStocks().size() : 0);

        return dto;
    }

    public Warehouse toEntity(WarehouseDto dto) {
        if (dto == null) {
            return null;
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setId(dto.getId());
        warehouse.setName(dto.getName());
        warehouse.setAddress(dto.getAddress());
        warehouse.setPhone(dto.getPhone());

        return warehouse;
    }
}