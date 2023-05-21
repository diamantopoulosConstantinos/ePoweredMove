package com.kosdiam.epoweredmove.models.dtos;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class POIDto implements Serializable {
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
    private UserDto userObj;
    private String chargingStationId;
    private ChargingStationDto chargingStationObj;
    private List<PaymentMethodDto> paymentMethodsObj;
    private Long timestamp;
    private Boolean availableSelectedVehicle;

}
