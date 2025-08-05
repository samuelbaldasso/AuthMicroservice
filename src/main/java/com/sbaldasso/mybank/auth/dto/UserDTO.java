package com.sbaldasso.mybank.auth.dto;

import com.sbaldasso.mybank.auth.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDTO {
    @NotNull(message = "Username is required")
    private String username;

    @NotNull(message = "Password is required")
    private String password;

    @NotNull(message = "Email is required")
    private String email;

    private String name;

    private Set<Role> roles;
}
