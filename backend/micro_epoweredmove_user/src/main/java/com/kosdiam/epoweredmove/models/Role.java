package com.kosdiam.epoweredmove.models;

import java.io.Serializable;

import com.kosdiam.epoweredmove.models.enums.UserRole;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Role implements Serializable {
    private UserRole name;
}
