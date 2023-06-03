package com.parsing.parsers.parserlot.openai.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LaptopModelVO {

    private String modelName;
    private String serialNumber;
    private String ramMemory;
    private String ssdMemory;
    private String hddMemory;
    private String processor;
    private String videoCard;
    private String monitorScale;
    private String monitorType;
    private String operationSystem;
}
