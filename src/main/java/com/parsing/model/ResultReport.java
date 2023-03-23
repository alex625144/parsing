package com.parsing.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "result_report")
public class ResultReport {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id")
    private UUID id;

    @Column(name = "dk")
    private String dk;

    @Column(name = "prozorro_url")
    private String prozorroURL;

    @Column(name = "lot_price")
    private BigDecimal lotPrice;

    @Column(name = "market_price")
    private BigDecimal marketPrice;

    @Column(name = "price_violation")
    private Integer priceViolation;

    @Column(name = "total_price_violation")
    private Integer totalPriceViolation;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "total_amount")
    private Integer totalAmount;
}
