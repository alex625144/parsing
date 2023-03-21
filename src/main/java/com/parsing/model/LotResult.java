package com.parsing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
@Table(name = "lot_result")
public class LotResult {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "dk")
    private String dk;

    @Column(name = "url")
    private String url;

    @Column(name = "pdf_link")
    private String pdfLink;

    @Enumerated
    @Column(name = "status")
    private Status status;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "parsing_date")
    private LocalDate parsingDate;
}
