package com.parsing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString(exclude = "lotPDFResult")
@Table(name = "lot_result")
public class LotResult {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    private Participant buyer;

    @ManyToOne
    private Participant seller;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private LotStatus lotStatus;

    @Column(name = "dk")
    private String dk;

    @Column(name = "lot_total_price")
    private BigDecimal lotTotalPrice;

    @ManyToMany
    private List<Participant> participants;

    @Column(name = "lot_url")
    private String lotURL;

    @Column(name = "pdf_url")
    private String pdfURL;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "parsing_date")
    private LocalDate parsingDate;

    @OneToOne(cascade = CascadeType.ALL)
    LotPDFResult lotPDFResult;
}
