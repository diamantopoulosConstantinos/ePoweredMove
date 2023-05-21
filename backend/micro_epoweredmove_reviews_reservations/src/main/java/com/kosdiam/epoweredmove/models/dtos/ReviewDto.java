package com.kosdiam.epoweredmove.models.dtos;

import java.io.Serializable;

import com.kosdiam.epoweredmove.models.enums.ReviewStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewDto implements Serializable {
    private String id;
    private String comments;
    private Integer rating;
    private ReviewStatus status;
    private Long timestamp;
    private String userId;
    private UserDto userObj;
    private String chargingStationId;
    private ChargingStationDto chargingStationObj;
}
