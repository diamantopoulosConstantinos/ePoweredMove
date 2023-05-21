package com.kosdiam.epoweredmove.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements Serializable {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private List<Role> roles;
}
