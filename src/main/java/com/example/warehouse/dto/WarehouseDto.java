package com.example.warehouse.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDto {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private int totalProducts;
}