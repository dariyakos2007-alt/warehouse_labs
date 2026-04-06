package com.example.warehouse.repository;

import com.example.warehouse.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    Optional<Category> findByName(String name);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products WHERE c.name = :name")
    Optional<Category> findByNameWithProducts(@Param("name") String name);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products " +
            "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Category> findByNameContainingIgnoreCaseWithProducts(@Param("name") String name);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products")
    List<Category> findAllWithProducts();
}
