package com.parsing.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lot_info")
public class LotInfo {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id")
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

    @OneToMany(mappedBy = "lotInfo", cascade = CascadeType.ALL)
    private List<LotItemInfo> lotItems;

    @OneToOne
    private LotResult lotResult;
}
