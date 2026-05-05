package com.example.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных для товара")
public class ProductDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long id;

    @NotBlank(message = "Название товара не может быть пустым")
    @Size(min = 2, max = 100, message = "Название товара должно быть от 2 до 100 символов")
    @Schema(description = "Название товара", example = "Ноутбук")
    private String name;

    @Positive(message = "Цена должна быть больше 0")
    @Schema(description = "Цена товара", example = "999.99")
    private Double price;

    @Schema(description = "ID категории", example = "5")
    private Long categoryId;

    @Schema(description = "Название категории", example = "Электроника")
    private String categoryName;

    @Schema(description = "ID поставщиков", example = "[1, 2, 3]")
    private Set<Long> supplierIds;

    @Schema(description = "Общий остаток на всех складах", example = "150")
    private Integer totalStock;
}