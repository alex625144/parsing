package com.parsing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
@JsonIgnoreProperties(ignoreUnknown = true)
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
    @Column(name = "lot_status")
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

    @Column(name = "date_modified")
    private ZonedDateTime dateModified;

    @OneToOne(cascade = CascadeType.ALL)
    LotPDFResult lotPDFResult;
}
