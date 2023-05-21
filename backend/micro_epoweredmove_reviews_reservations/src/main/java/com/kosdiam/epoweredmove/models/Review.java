package com.kosdiam.epoweredmove.models;

import java.io.Serializable;

import com.kosdiam.epoweredmove.models.enums.ReviewStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Review implements Serializable {
    private String id;
    private String comments;
    private Integer rating;
    private ReviewStatus status;
    private String userId;
    private String chargingStationId;
    private Long timestamp;
}
