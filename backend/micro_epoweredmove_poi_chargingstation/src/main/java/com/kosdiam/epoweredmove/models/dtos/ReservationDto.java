package com.kosdiam.epoweredmove.models.dtos;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationDto implements Serializable {
    private String id;
    private Boolean accepted;
    private Long timeStart;
    private Long timeEnd;
    private String vehicleId;
    private VehicleDto vehicleObj;
    private String userId;
    private UserDto userObj;
    private String plugId;
    private PlugDto plugObj;
    private Long timestamp;
    private Boolean cancelled;
    private Float poiLatitude;
    private Float poiLongitude;
}
