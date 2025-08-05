package com.sbaldasso.mybank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbaldasso.mybank.auth.controllers.AuthController;
import com.sbaldasso.mybank.auth.dto.UserLoginDTO;
import com.sbaldasso.mybank.auth.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Use @WebMvcTest para focar apenas na camada web (controllers, filtros, etc)
// Especifique o controller que você quer testar.
@WebMvcTest(AuthController.class)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Permite simular requisições HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos para JSON

    // Use @MockBean para adicionar mocks ao contexto do Spring
    @MockitoBean
    private AuthService authService;

    // NOTA: Precisaremos de mocks para as dependências do SecurityConfiguration também
    // já que o @WebMvcTest carrega a configuração de segurança.
    @MockitoBean
    @Qualifier("customUserDetailsService")
    private com.sbaldasso.mybank.auth.services.CustomUserDetailsService userDetailsService;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @MockitoBean
    private com.sbaldasso.mybank.auth.security.jwt.JWTTokenGenerator jwtTokenGenerator;

    @MockitoBean
    private com.sbaldasso.mybank.auth.repositories.UserRepository userRepository;


    @Test
    @DisplayName("POST /login - Invalid Credentials - Integration Test")
    void login_whenCredentialsAreInvalid_shouldReturnUnauthorized() throws Exception {
        // --- GIVEN ---
        UserLoginDTO loginDTO = UserLoginDTO.builder().email("wrong@user.com")
                .password("wrongpass").build();

        // Mock o authService para lançar a exceção, como antes
        given(authService.authenticate(any(UserLoginDTO.class)))
                .willThrow(new BadCredentialsException("Invalid username or password"));

        // --- WHEN & THEN ---
        // Simule uma requisição POST para /api/v1/auth/login
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                // Espere que o status seja 401 Unauthorized, porque o ControllerAdvice vai atuar
                .andExpect(status().isUnauthorized());
    }
}