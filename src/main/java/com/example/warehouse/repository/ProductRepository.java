package com.example.warehouse.repository;

import com.example.warehouse.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.suppliers " +
            "LEFT JOIN FETCH p.stocks " +
            "WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.suppliers " +
            "LEFT JOIN FETCH p.stocks " +
            "LEFT JOIN FETCH p.category")
    List<Product> findAllWithStocksAndCategory();

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH p.suppliers s " +
            "LEFT JOIN FETCH p.stocks st " +
            "WHERE c.name = :categoryName AND p.price <= :maxPrice")
    Page<Product> findByCategoryAndMaxPriceWithFetch(@Param("categoryName") String categoryName,
                                                     @Param("maxPrice") Double maxPrice,
                                                     Pageable pageable);

    @Query(value = "SELECT p.id FROM products p " +
            "INNER JOIN categories c ON p.category_id = c.id " +
            "WHERE c.name = :categoryName AND p.price <= :maxPrice " +
            "ORDER BY p.name ASC",
            countQuery = "SELECT COUNT(p.id) FROM products p " +
                    "INNER JOIN categories c ON p.category_id = c.id " +
                    "WHERE c.name = :categoryName AND p.price <= :maxPrice",
            nativeQuery = true)
    Page<Long> findProductIdsByCategoryAndMaxPriceNative(@Param("categoryName") String categoryName,
                                                         @Param("maxPrice") Double maxPrice,
                                                         Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.suppliers " +
            "LEFT JOIN FETCH p.stocks " +
            "WHERE p.id IN :ids")
    List<Product> findAllWithDetailsByIds(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.suppliers " +
            "LEFT JOIN FETCH p.stocks " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCaseWithDetails(@Param("name") String name);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.suppliers " +
            "LEFT JOIN FETCH p.stocks " +
            "WHERE p.category.id = :categoryId")
    List<Product> findByCategoryIdWithDetails(@Param("categoryId") Long categoryId);
}