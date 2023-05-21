package com.kosdiam.epoweredmove.models.dtos;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto implements Serializable {
    private String id;
    @Size(min = 2, max = 30)
    private String name;
    @Size(min = 2, max = 30)
    private String surname;
    @Email
    @NotNull
    private String email;
    private String phone;
    @Size(min = 8, max = 30)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
    @Nullable
    private String password;
    private List<RoleDto> roles;
}
