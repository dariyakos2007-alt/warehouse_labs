package com.example.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных для склада")
public class WarehouseDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long id;

    @NotBlank(message = "Название склада не может быть пустым")
    @Schema(description = "Название склада", example = "Склад №1")
    private String name;

    @Schema(description = "Адрес склада", example = "г. Минск, ул. Промышленная, 10")
    private String address;

    @Pattern(regexp = "^\\+\\d{12}$", message = "Телефон должен быть в формате +XXXXXXXXXXXX (ровно 12 цифр)")
    @Schema(description = "Телефон склада", example = "+375173331122")
    private String phone;

    @Schema(description = "Количество товаров на складе", example = "45")
    private Integer totalProducts;
}