package com.example.warehouse.repository;

import com.example.warehouse.model.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByName(String name);

    List<Supplier> findByNameContainingIgnoreCase(String namePart);

    Optional<Supplier> findByEmail(String email);

    @Query("SELECT DISTINCT s FROM Supplier s LEFT JOIN FETCH s.products WHERE s.id = :id")
    Optional<Supplier> findByIdWithProducts(Long id);
}