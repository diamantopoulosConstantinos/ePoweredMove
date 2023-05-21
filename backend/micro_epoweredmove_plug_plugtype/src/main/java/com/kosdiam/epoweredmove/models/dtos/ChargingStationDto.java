package com.kosdiam.epoweredmove.models.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChargingStationDto {
    private String id;
    private Float pricePerKwh;
    private Boolean autoAccept; //reserve without owner permission
    private Boolean barcodeEnabled;
    private Boolean apiEnabled;
    private String imageId;
}
