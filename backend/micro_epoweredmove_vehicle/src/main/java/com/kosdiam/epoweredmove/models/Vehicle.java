package com.kosdiam.epoweredmove.models;

import java.io.Serializable;

import com.kosdiam.epoweredmove.models.enums.EvType;
import com.kosdiam.epoweredmove.models.enums.VehicleType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Vehicle implements Serializable {
    private String id;
    private String brand;
    private VehicleType vehicleType;
    private EvType evType;
    private String model;
    private Integer releaseYear;
    private Float usableBatterySize;
    private String plugTypeId;
    private Float avgConsumption;
    private String userId;
    private Long timestamp;
}
