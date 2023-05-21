package com.kosdiam.epoweredmove.models.dtos;

import java.io.Serializable;

import com.kosdiam.epoweredmove.models.enums.CurrentType;
import com.kosdiam.epoweredmove.models.enums.TeslaCompatible;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlugTypeDto implements Serializable {
    private String id;
    private String connector;
    private CurrentType current;
    private String typeLevel;
    private String description;
    private String compatibility;
    private TeslaCompatible tesla;
    private String imageId;
}
