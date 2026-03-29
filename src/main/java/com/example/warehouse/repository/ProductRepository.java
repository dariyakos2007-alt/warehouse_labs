package com.example.warehouse.repository;

import com.example.warehouse.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.suppliers")
    List<Product> findAllWithDetails();

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.suppliers " +
            "LEFT JOIN FETCH p.stocks " +
            "LEFT JOIN FETCH p.category")
    List<Product> findAllWithStocksAndCategory();

    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName AND p.price <= :maxPrice")
    List<Product> findByCategoryAndMaxPrice(@Param("categoryName") String categoryName,
                                            @Param("maxPrice") Double maxPrice);

    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName AND p.price <= :maxPrice")
    Page<Product> findByCategoryAndMaxPricePaged(@Param("categoryName") String categoryName,
                                                 @Param("maxPrice") Double maxPrice,
                                                 Pageable pageable);

    @Query(value = "SELECT p.* FROM products p " +
            "INNER JOIN categories c ON p.category_id = c.id " +
            "WHERE c.name = :categoryName AND p.price <= :maxPrice",
            nativeQuery = true)
    List<Product> findByCategoryAndMaxPriceNative(@Param("categoryName") String categoryName,
                                                  @Param("maxPrice") Double maxPrice);

    @Query(value = "SELECT p.* FROM products p " +
            "INNER JOIN categories c ON p.category_id = c.id " +
            "WHERE c.name = :categoryName AND p.price <= :maxPrice",
            countQuery = "SELECT COUNT(*) FROM products p " +
                    "INNER JOIN categories c ON p.category_id = c.id " +
                    "WHERE c.name = :categoryName AND p.price <= :maxPrice",
            nativeQuery = true)
    Page<Product> findByCategoryAndMaxPriceNativePaged(@Param("categoryName") String categoryName,
                                                       @Param("maxPrice") Double maxPrice,
                                                       Pageable pageable);
}