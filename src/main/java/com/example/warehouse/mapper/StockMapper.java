package com.example.warehouse.mapper;

import com.example.warehouse.dto.StockDto;
import com.example.warehouse.model.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    public StockDto toDto(Stock stock) {
        if (stock == null) {
            return null;
        }

        StockDto dto = new StockDto();
        dto.setId(stock.getId());
        dto.setQuantity(stock.getQuantity());
        dto.setMaxQuantity(stock.getMaxQuantity());
        dto.setLastUpdated(stock.getLastUpdated());
        dto.setOverStock(stock.isOverStock());

        if (stock.getProduct() != null) {
            dto.setProductId(stock.getProduct().getId());
            dto.setProductName(stock.getProduct().getName());
        }

        if (stock.getWarehouse() != null) {
            dto.setWarehouseId(stock.getWarehouse().getId());
            dto.setWarehouseName(stock.getWarehouse().getName());
        }

        return dto;
    }

    public Stock toEntity(StockDto dto) {
        if (dto == null) {
            return null;
        }

        Stock stock = new Stock();
        stock.setId(dto.getId());
        stock.setQuantity(dto.getQuantity());
        stock.setMaxQuantity(dto.getMaxQuantity());
        stock.setLastUpdated(dto.getLastUpdated());

        return stock;
    }
}