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

    @Query("SELECT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.warehouse WHERE s.id = :id")
    Optional<Stock> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.warehouse")
    List<Stock> findAllWithDetails();

    @Query("SELECT s FROM Stock s " +
            "LEFT JOIN FETCH s.warehouse " +
            "LEFT JOIN FETCH s.product")
    List<Stock> findAllWithWarehouseAndProduct();

    @Query("SELECT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.warehouse " +
            "WHERE s.product.id = :productId")
    List<Stock> findByProductIdWithDetails(@Param("productId") Long productId);

    @Query("SELECT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.warehouse " +
            "WHERE s.warehouse.id = :warehouseId")
    List<Stock> findByWarehouseIdWithDetails(@Param("warehouseId") Long warehouseId);

    @Query("SELECT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.warehouse " +
            "WHERE s.product.id = :productId AND s.warehouse.id = :warehouseId")
    Optional<Stock> findByProductIdAndWarehouseIdWithDetails(@Param("productId") Long productId,
                                                             @Param("warehouseId") Long warehouseId);

    @Query("SELECT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.warehouse " +
            "WHERE s.maxQuantity IS NOT NULL AND s.quantity > s.maxQuantity")
    List<Stock> findOverStockWithDetails();
}
