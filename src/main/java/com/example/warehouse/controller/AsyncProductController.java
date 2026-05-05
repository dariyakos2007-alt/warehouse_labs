package com.example.warehouse.controller;

import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.service.AsyncProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/async")
@RequiredArgsConstructor
public class AsyncProductController {

    private final AsyncProductService asyncProductService;

    @PostMapping("/products")
    public ResponseEntity<Map<String, Long>> createProductsAsync(@RequestBody List<ProductDto> products) {
        Long taskId = asyncProductService.startAsyncTask(products);
        return ResponseEntity.ok(Map.of("taskId", taskId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Map<String, String>> getTaskStatus(@PathVariable Long taskId) {
        String status = asyncProductService.getTaskStatus(taskId);
        return ResponseEntity.ok(Map.of("status", status));
    }
}