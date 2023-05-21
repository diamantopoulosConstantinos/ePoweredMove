package com.kosdiam.epoweredmove.models.dtos;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RouteInfoDto implements Serializable {
    private Long destinationMeters;
    private Long metersByVehicle;
    private Long metersByFoot;
}
