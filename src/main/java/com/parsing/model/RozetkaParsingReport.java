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
@Table(name = "rozetka_parsing_report")
public class RozetkaParsingReport {

    @jakarta.persistence.Id
    @GeneratedValue(generator = "UUID")
    @Column(name ="id")
    private UUID Id;

    @Column(name = "model")
    private String model;

    @Column(name ="search_url")
    private String searchURL;

    @Column(name = "laptop_url")
    private String laptopURL;

    @Column(name = "market_price")
    private BigDecimal marketPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "lot_result")
    private LotResult lotResult;

    @Enumerated
    @Column(name = "status")
    private Status status;
}
