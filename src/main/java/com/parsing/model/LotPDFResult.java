package com.parsing.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lot_pdf_result")
public class    LotPDFResult {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name ="id")
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lot_result_id", referencedColumnName = "id")
    @JsonBackReference
    private LotResult lotResult;

    @OneToMany(mappedBy = "lotPDFResult")
    private List<LaptopItem> laptopItems;

    @Column(name = "parsing_date")
    private LocalDate parsingDate;
}