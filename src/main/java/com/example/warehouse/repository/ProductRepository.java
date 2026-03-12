package com.example.warehouse.repository;

import com.example.warehouse.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByPriceLessThan(double price);

    List<Product> findByPriceGreaterThan(double price);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.suppliers")
    List<Product> findAllWithDetails();

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.stocks")
    List<Product> findAllWithStocks();

    boolean existsByCategoryId(Long categoryId);
}