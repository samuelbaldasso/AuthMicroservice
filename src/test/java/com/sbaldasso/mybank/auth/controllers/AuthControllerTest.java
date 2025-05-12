package com.sbaldasso.mybank.auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.dto.UserLoginDTO;
import com.sbaldasso.mybank.auth.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private UserLoginDTO userLoginDTO;

    @BeforeEach
    public void setup() {
        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("password")
                .build();

        userLoginDTO = UserLoginDTO.builder()
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        Mockito.when(authService.register(any(UserDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User registered successfully")));
    }

    // Duplicate email test requires full context and UserRepository mocking, better for integration test

    @Test
    public void testLogin_Success() throws Exception {
        Mockito.when(authService.authenticate(any(UserLoginDTO.class)))
                .thenReturn(com.sbaldasso.mybank.auth.dto.UserAuthDTO.builder()
                        .token("fake-jwt-token")
                        .build());

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("fake-jwt-token"));
    }
}
