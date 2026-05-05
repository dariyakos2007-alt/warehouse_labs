package com.example.warehouse.repository;

import com.example.warehouse.model.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByPhone(String phone);

    Optional<Supplier> findByEmail(String email);

    Optional<Supplier> findByName(String name);

    @Query("SELECT DISTINCT s FROM Supplier s LEFT JOIN FETCH s.products WHERE s.id = :id")
    Optional<Supplier> findByIdWithProducts(Long id);

    @Query("SELECT DISTINCT s FROM Supplier s LEFT JOIN FETCH s.products")
    List<Supplier> findAllWithProducts();

    @Query("SELECT DISTINCT s FROM Supplier s LEFT JOIN FETCH s.products WHERE s.name = :name")
    Optional<Supplier> findByNameWithProducts(@Param("name") String name);

    @Query("SELECT DISTINCT s FROM Supplier s LEFT JOIN FETCH s.products WHERE s.email = :email")
    Optional<Supplier> findByEmailWithProducts(@Param("email") String email);

    @Query("SELECT DISTINCT s FROM Supplier s LEFT JOIN FETCH s.products " +
            "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Supplier> findByNameContainingIgnoreCaseWithProducts(@Param("name") String name);
}
