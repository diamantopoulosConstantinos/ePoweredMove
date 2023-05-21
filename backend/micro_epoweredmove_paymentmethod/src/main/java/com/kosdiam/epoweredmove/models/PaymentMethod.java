package com.kosdiam.epoweredmove.models;

import java.io.Serializable;

import com.kosdiam.epoweredmove.models.enums.PaymentType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentMethod implements Serializable {
    private String id;
    private PaymentType description;
    private String poiId;
}
