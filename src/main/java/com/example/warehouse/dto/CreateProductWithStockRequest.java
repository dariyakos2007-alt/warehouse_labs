package com.example.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание товара с остатком")
public class CreateProductWithStockRequest {

    @Valid
    @NotNull(message = "Данные о товаре обязательны")
    @Schema(description = "Данные товара")
    private ProductDto product;

    @NotNull(message = "ID склада обязателен")
    @Schema(description = "ID склада", example = "1")
    private Long warehouseId;

    @NotNull(message = "Количество обязательно")
    @Positive(message = "Количество должно быть положительным")
    @Schema(description = "Количество товара", example = "50")
    private Integer quantity;
}