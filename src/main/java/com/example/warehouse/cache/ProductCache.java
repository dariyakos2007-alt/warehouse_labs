package com.example.warehouse.cache;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.warehouse.dto.ProductDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProductCache {

    private final Map<ProductSearchKey, Page<ProductDto>> cache = new ConcurrentHashMap<>();

    public Page<ProductDto> get(ProductSearchKey key) {
        return cache.get(key);
    }

    public void put(ProductSearchKey key, Page<ProductDto> value) {
        cache.put(key, value);
    }

    public void clearAll() {
        cache.clear();
    }
}