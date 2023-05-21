package com.kosdiam.epoweredmove.models.dtos;

import java.io.Serializable;

import com.kosdiam.epoweredmove.models.enums.UserRole;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleDto implements Serializable {
    private UserRole name;
}