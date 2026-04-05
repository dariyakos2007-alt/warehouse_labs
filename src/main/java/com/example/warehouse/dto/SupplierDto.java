package com.example.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных для поставщика")
public class SupplierDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long id;

    @NotBlank(message = "Название поставщика не может быть пустым")
    @Size(min = 2, max = 100, message = "Название поставщика должно быть от 2 до 100 символов")
    @Schema(description = "Название поставщика", example = "ООО ТехноПоставка")
    private String name;

    @NotBlank(message = "Контактное лицо не может быть пустым")
    @Schema(description = "Контактное лицо", example = "Иванов Иван")
    private String contactPerson;

    @Pattern(regexp = "^\\+\\d{12}$", message = "Телефон должен быть в формате +XXXXXXXXXXXX (ровно 12 цифр)")
    @Schema(description = "Телефон", example = "+375447973155")
    private String phone;

    @Email(message = "Email должен быть корректным")
    @NotBlank(message = "Email не может быть пустым")
    @Schema(description = "Email", example = "info@techno.by")
    private String email;

    @Schema(description = "Адрес", example = "г. Минск, ул. Ленина, 1")
    private String address;

    @Schema(description = "Количество поставляемых товаров", example = "15")
    private Integer productCount;
}