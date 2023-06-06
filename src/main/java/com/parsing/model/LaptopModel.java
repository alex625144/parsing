package com.parsing.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "laptop_model")
public class LaptopModel {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "ram_memory")
    private String ramMemory;

    @Column(name = "ssd_memory")
    private String ssdMemory;

    @Column(name = "hdd_memory")
    private String hddMemory;

    @Column(name = "processor")
    private String processor;

    @Column(name = "video_card")
    private String videoCard;

    @Column(name = "monitor_scale")
    private String monitorScale;

    @Column(name = "monitor_type")
    private String monitorType;

    @Column(name = "operation_system")
    private String operationSystem;

    @OneToOne
    private LaptopItem laptopItem;
}

