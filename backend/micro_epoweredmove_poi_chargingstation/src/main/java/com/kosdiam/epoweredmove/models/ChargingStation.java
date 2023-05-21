package com.kosdiam.epoweredmove.models;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChargingStation implements Serializable {

    private String id;
    private Float pricePerKwh;
    private Boolean autoAccept; //reserve without owner permission
    private Boolean barcodeEnabled;
    private Boolean apiEnabled;
    private String imageId;
}
