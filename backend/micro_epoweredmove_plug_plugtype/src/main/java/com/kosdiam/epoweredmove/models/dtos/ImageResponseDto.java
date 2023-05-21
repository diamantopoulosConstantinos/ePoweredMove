package com.kosdiam.epoweredmove.models.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageResponseDto {
    private String imageId;
    private Boolean isDeleted;
}
