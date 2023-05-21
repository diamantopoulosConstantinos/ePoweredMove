package com.kosdiam.epoweredmove.models.dtos;

import java.io.Serializable;

import com.kosdiam.epoweredmove.models.enums.Availability;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlugDto implements Serializable {
    private String id;
    private Availability availability;
    private Float power;
    private Integer phases;
    private String chargingStationId;
    private ChargingStationDto chargingStationObj;
    private Long timestamp;
    private String plugTypeId;
    private PlugTypeDto plugTypeObj;
}
