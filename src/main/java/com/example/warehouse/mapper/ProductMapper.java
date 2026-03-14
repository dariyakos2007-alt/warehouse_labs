package com.example.warehouse.mapper;

import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.model.entity.Stock;
import com.example.warehouse.model.entity.Supplier;  // ← ДОБАВЛЕНО!
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
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

    public List<ProductDto> toDtoList(List<Product> products) {
        if (products == null) {
            return List.of();
        }
        return products.stream()
                .map(this::toDto)
                .toList();
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