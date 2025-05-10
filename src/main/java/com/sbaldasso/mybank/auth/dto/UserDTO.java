package com.sbaldasso.mybank.auth.dto;

import com.sbaldasso.mybank.auth.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private String name;
    private Set<Role> roles;
}
