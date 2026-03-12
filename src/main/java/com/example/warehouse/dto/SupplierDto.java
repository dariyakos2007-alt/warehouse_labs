package com.example.warehouse.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {

    private Long id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private int productCount;
}