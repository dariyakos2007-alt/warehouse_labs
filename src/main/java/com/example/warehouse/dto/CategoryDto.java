package com.example.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных для категории")
public class CategoryDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long id;

    @NotBlank(message = "Название категории не может быть пустым")
    @Size(min = 2, max = 100, message = "Название категории должно быть от 2 до 100 символов")
    @Schema(description = "Название категории", example = "Электроника")
    private String name;

    @Schema(description = "Описание категории", example = "Электронные устройства и аксессуары")
    private String description;

    @Schema(description = "Количество товаров в категории", example = "25")
    private Integer productCount;
}