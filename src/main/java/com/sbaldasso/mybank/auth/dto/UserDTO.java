package com.sbaldasso.mybank.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private String name;

}
