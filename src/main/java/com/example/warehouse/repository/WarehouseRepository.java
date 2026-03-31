package com.example.warehouse.repository;

import com.example.warehouse.model.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query("SELECT DISTINCT w FROM Warehouse w " +
            "LEFT JOIN FETCH w.stocks s " +
            "LEFT JOIN FETCH s.product")
    List<Warehouse> findAllWithStocks();

    @Query("SELECT DISTINCT w FROM Warehouse w " +
            "LEFT JOIN FETCH w.stocks s " +
            "LEFT JOIN FETCH s.product " +
            "WHERE w.name = :name")
    Optional<Warehouse> findByNameWithStocks(@Param("name") String name);

    @Query("SELECT DISTINCT w FROM Warehouse w " +
            "LEFT JOIN FETCH w.stocks s " +
            "LEFT JOIN FETCH s.product " +
            "WHERE w.id = :id")
    Optional<Warehouse> findByIdWithStocks(@Param("id") Long id);
}
