package com.example.warehouse.dto;

import lombok.Data;

@Data
public class CreateProductWithStockRequest {
    private ProductDto product;
    private Long warehouseId;
    private Integer quantity;
}