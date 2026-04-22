package com.example.warehouse.service;

import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.mapper.ProductMapper;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final Map<Long, String> taskStatus = new ConcurrentHashMap<>();
    private final AtomicLong taskIdGenerator = new AtomicLong(1);

    public Long startAsyncTask(List<ProductDto> productDtos) {
        Long taskId = taskIdGenerator.getAndIncrement();

        taskStatus.put(taskId, "IN_PROGRESS");
        log.info("Запущена асинхронная задача {} для создания {} товаров", taskId, productDtos.size());

        CompletableFuture.runAsync(() -> processTask(taskId, productDtos))
                .exceptionally(ex -> {
                    log.error("Ошибка при выполнении задачи {}: {}", taskId, ex.getMessage());
                    taskStatus.put(taskId, "FAILED");
                    return null;
                });

        return taskId;
    }

    private void processTask(Long taskId, List<ProductDto> productDtos) {
        try {
            List<Product> products = productDtos.stream()
                    .map(productMapper::toEntity)
                    .toList();
            productRepository.saveAll(products);

            Thread.sleep(10000);
            taskStatus.put(taskId, "COMPLETED");
            log.info("Задача {} завершена успешно", taskId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            taskStatus.put(taskId, "FAILED");
            log.error("Задача {} прервана", taskId, e);
        } catch (Exception e) {
            taskStatus.put(taskId, "FAILED");
            log.error("Ошибка при выполнении задачи {}: {}", taskId, e.getMessage());
        }
    }

    public String getTaskStatus(Long taskId) {
        return taskStatus.getOrDefault(taskId, "NOT_FOUND");
    }
}