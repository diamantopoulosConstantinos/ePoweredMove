package com.kosdiam.epoweredmove.models;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class POI implements Serializable {
    private String id;
    private Float latitude;
    private Float longitude;
    private Boolean parking;
    private Boolean illumination;
    private Boolean wc;
    private Boolean shopping;
    private Boolean food;
    private String phone;
    private String userId;
    private String chargingStationId;
    private Long timestamp;
}
