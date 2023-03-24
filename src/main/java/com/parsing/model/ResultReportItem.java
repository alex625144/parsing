package com.parsing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Setter
@Getter
@ToString(exclude = "resultReport")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "result_report")
public class ResultReportItem {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "Id")
    private UUID Id;

    @Column(name = "model")
    private String model;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "market_price")
    private BigDecimal marketPrice;

    @Column(name = "price_violation")
    private BigDecimal priceViolation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_report_id")
    private ResultReport resultReport;
}
