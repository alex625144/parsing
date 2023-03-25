package com.parsing.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lot_pdf_result")
public class LotPDFResult {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name ="id")
    private UUID id;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "amount")
    private int amount;

    @Column(name = "total_amount")
    private int totalAmount;

    @Column(name = "model")
    private String model;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "LotResult.id", referencedColumnName = "id")
    private LotResult lot;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lotPDFResult")
    private List<LaptopItem> laptopItems;

}