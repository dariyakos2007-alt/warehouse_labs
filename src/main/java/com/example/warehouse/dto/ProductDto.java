package com.example.warehouse.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String name;
    private Double price;
    private Long categoryId;
    private String categoryName;
    private Set<Long> supplierIds;
    private Integer totalStock;
}