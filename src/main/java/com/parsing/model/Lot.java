package com.parsing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "Lot")
public class Lot {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "DK")
    private String dk;

    @Column(name = "URL")
    private String url;

    @Column(name = "PDFLink")
    private String pdfLink;

    @Column(name = "Status")
    private Status status;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "parsingDate")
    private LocalDate parsingDate;
}
