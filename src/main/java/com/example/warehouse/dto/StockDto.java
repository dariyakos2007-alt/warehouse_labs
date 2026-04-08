package com.example.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных для остатков товара")
public class StockDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long id;

    @Positive(message = "Количество товара должно быть больше 0")
    @Schema(description = "Количество товара", example = "100")
    private Integer quantity;

    @Positive(message = "Максимальное количество должно быть больше 0")
    @Schema(description = "Максимальное количество (превышение)", example = "200")
    private Integer maxQuantity;

    @Schema(description = "Дата последнего обновления", example = "2026-04-05T15:30:00")
    private LocalDateTime lastUpdated;

    @Schema(description = "ID товара", example = "10")
    private Long productId;

    @Schema(description = "Название товара", example = "Ноутбук")
    private String productName;

    @Schema(description = "ID склада", example = "3")
    private Long warehouseId;

    @Schema(description = "Название склада", example = "Склад №1")
    private String warehouseName;

    @Schema(description = "Превышение максимального количества", example = "false")
    private Boolean overStock;

    public boolean isOverStock() {
        return maxQuantity != null && quantity > maxQuantity;
    }
}