package com.example.warehouse.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product", "warehouse"})
@EqualsAndHashCode(exclude = {"product", "warehouse"})
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private Integer minQuantity;
    private Integer maxQuantity;
    private LocalDateTime lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    public Stock(Product product, Warehouse warehouse, Integer quantity) {
        this.product = product;
        this.warehouse = warehouse;
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    public Stock(Product product, Warehouse warehouse, Integer quantity,
                 Integer minQuantity, Integer maxQuantity) {
        this.product = product;
        this.warehouse = warehouse;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isLowStock() {
        return minQuantity != null && quantity < minQuantity;
    }

    public boolean isOverStock() {
        return maxQuantity != null && quantity > maxQuantity;
    }

    public void addQuantity(Integer amount) {
        this.quantity += amount;
        this.lastUpdated = LocalDateTime.now();
    }

    public void removeQuantity(Integer amount) {
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Недостаточно товара на складе");
        }
        this.quantity -= amount;
        this.lastUpdated = LocalDateTime.now();
    }
}