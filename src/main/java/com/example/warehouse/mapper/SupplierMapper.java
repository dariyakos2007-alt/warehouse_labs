package com.example.warehouse.mapper;

import com.example.warehouse.dto.SupplierDto;
import com.example.warehouse.model.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierDto toDto(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierDto dto = new SupplierDto();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setPhone(supplier.getPhone());
        dto.setEmail(supplier.getEmail());
        dto.setAddress(supplier.getAddress());
        dto.setProductCount(supplier.getProducts() != null ? supplier.getProducts().size() : 0);

        return dto;
    }

    public Supplier toEntity(SupplierDto dto) {
        if (dto == null) {
            return null;
        }

        Supplier supplier = new Supplier();
        supplier.setId(dto.getId());
        supplier.setName(dto.getName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());

        return supplier;
    }
}