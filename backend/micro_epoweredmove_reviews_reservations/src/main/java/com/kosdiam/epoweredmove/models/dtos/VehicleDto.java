package com.kosdiam.epoweredmove.models.dtos;

import java.io.Serializable;

import com.google.maps.model.VehicleType;
import com.kosdiam.epoweredmove.models.enums.EvType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleDto implements Serializable {
    private String id;
    private String brand;
    private VehicleType vehicleType;
    private EvType evType;
    private String model;
    private Integer releaseYear;
    private Float usableBatterySize;
    private String plugTypeId;
    private PlugTypeDto plugTypeObj;
    private Float avgConsumption;
    private String userId;
    private UserDto userObj;
    private Long timestamp;
}
