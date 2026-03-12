package com.example.warehouse.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {

    private Long id;
    private int quantity;
    private Integer minQuantity;
    private Integer maxQuantity;
    private LocalDateTime lastUpdated;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private boolean lowStock;
    private boolean overStock;
}