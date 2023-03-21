package com.parsing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "result_report")
public class ResultReport {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id")
    UUID id;

    @Column(name = "dk")
    String dk;

    @Column(name ="model")
    String model;

    @Column(name = "prozorro_url")
    String prozorroUrl;

    @Column(name = "lot_price")
    BigDecimal lotPrice;

    @Column(name = "market_price")
    BigDecimal marketPrice;

    @Column(name = "price_violation")
    int priceViolation;

    @Column(name = "total_price_violation")
    int totalPriceViolation;

    @Column(name = "amount")
    int amount;

    @Column(name = "total_amount")
    int totalAmount;
}
