package com.example.warehouse.cache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class ProductSearchKey {
    private final String categoryName;
    private final Double maxPrice;
    private final int page;
    private final int size;
    private final String sortField;
}