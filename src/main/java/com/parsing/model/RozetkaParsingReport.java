package com.parsing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import lombok.Setter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "rozetka_parsing_report")
public class RozetkaParsingReport {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id")
    private UUID Id;

    @Column(name = "model")
    private String model;

    @Column(name = "search_url")
    private String searchURL;

    @Column(name = "laptop_url")
    private String laptopURL;

    @Column(name = "market_price")
    private BigDecimal marketPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LotResult.id", referencedColumnName = "id")
    private LotResult lotResult;
}
