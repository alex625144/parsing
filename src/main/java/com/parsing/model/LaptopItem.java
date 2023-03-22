package com.parsing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "laptop_item")
public class LaptopItem {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    UUID itemId;

    @Column(name = "model")
    String model;

    @Column(name = "amount")
    int amount;

    @Column(name = "price")
    BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    private LotPDFResult lotPDFResult;
}
