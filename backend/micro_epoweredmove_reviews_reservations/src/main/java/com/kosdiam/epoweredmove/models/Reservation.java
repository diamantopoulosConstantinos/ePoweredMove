package com.kosdiam.epoweredmove.models;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Reservation implements Serializable {
    private String id;
    private Boolean accepted;
    private Long timeStart;
    private Long timeEnd;
    private String vehicleId;
    private String userId;
    private String plugId;
    private Long timestamp;
    private Boolean cancelled;
}
