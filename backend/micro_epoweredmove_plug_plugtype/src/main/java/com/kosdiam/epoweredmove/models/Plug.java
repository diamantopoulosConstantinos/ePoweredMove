package com.kosdiam.epoweredmove.models;

import java.io.Serializable;

import com.kosdiam.epoweredmove.models.enums.Availability;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Plug implements Serializable {
    private String id;
    private Availability availability;
    private Float power;
    private Integer phases;
    private String chargingStationId;
    private Long timestamp;
    private String plugTypeId;

}
