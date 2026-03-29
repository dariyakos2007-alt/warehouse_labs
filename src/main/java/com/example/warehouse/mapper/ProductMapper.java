package com.example.warehouse.mapper;

import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.model.entity.Stock;
import com.example.warehouse.model.entity.Supplier;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        if (product.getSuppliers() != null) {
            dto.setSupplierIds(product.getSuppliers().stream()
                    .map(Supplier::getId)
                    .collect(Collectors.toSet()));
        }

        if (product.getStocks() != null) {
            int total = product.getStocks().stream()
                    .mapToInt(Stock::getQuantity)
                    .sum();
            dto.setTotalStock(total);
        }

        return dto;
    }

    public Product toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        return product;
    }
}