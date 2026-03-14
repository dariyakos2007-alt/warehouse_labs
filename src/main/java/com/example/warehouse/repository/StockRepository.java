package com.example.warehouse.repository;

import com.example.warehouse.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    List<Stock> findByProductId(Long productId);

    List<Stock> findByWarehouseId(Long warehouseId);

    @Query("SELECT s FROM Stock s WHERE s.minQuantity IS NOT NULL AND s.quantity < s.minQuantity")
    List<Stock> findLowStock();

    @Query("SELECT s FROM Stock s WHERE s.maxQuantity IS NOT NULL AND s.quantity > s.maxQuantity")
    List<Stock> findOverStock();

    @Query("SELECT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.warehouse WHERE s.id = :id")
    Optional<Stock> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.warehouse")
    List<Stock> findAllWithDetails();
}