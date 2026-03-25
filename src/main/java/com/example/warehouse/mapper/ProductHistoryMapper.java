package com.example.warehouse.mapper;

import com.example.warehouse.dto.ProductHistoryDto;
import com.example.warehouse.model.entity.ProductHistory;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class ProductHistoryMapper {

    public ProductHistoryDto toDto(ProductHistory history) {
        if (history == null) {
            return null;
        }
        return ProductHistoryDto.builder()
                .id(history.getId())
                .createdDate(history.getCreatedDate())
                .createdBy(history.getCreatedBy())
                .description(history.getDescription())
                .build();
    }

    public ProductHistory toEntity(ProductHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return ProductHistory.builder()
                .id(dto.getId())
                .createdDate(dto.getCreatedDate() != null ? dto.getCreatedDate() : LocalDateTime.now())
                .createdBy(dto.getCreatedBy())
                .description(dto.getDescription())
                .build();
    }
}