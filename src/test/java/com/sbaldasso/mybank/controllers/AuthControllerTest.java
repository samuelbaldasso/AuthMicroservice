package com.sbaldasso.mybank.controllers;

import com.sbaldasso.mybank.auth.controllers.AuthController;
import com.sbaldasso.mybank.auth.dto.UserAuthDTO;
import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.dto.UserLoginDTO;
import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import com.sbaldasso.mybank.auth.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


/**
 * Pure Unit tests for the AuthController using only Mockito.
 * This approach tests the controller's logic in isolation without loading the Spring context.
 */
public class AuthControllerTest {

    // Mocks are created for all dependencies of AuthController
    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    // The class under test. Mocks will be injected into this instance.
    @InjectMocks
    private AuthController authController;

    private UserDTO userDTO;
    private UserLoginDTO userLoginDTO;
    private UserAuthDTO userAuthDTO;
    private User existingUser;

    @BeforeEach
    void setUp() {
        // Initializes mocks and injects them into authController
        MockitoAnnotations.openMocks(this);

        // Setup common test data
        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();

        userLoginDTO = UserLoginDTO.builder()
                .email("test@example.com")
                .password("password123").build();

        userAuthDTO = UserAuthDTO.builder()
                .token("dummy.jwt.token")
                .build();

        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("POST /register - Success")
    void registerUser_whenEmailNotExists_shouldReturnCreated() {
        // --- GIVEN ---
        // Mocking the behavior of our dependencies for this test case
        given(userRepository.findByEmail(userDTO.getEmail())).willReturn(Optional.empty());
        given(authService.register(any(UserDTO.class))).willReturn(userAuthDTO);

        // --- WHEN ---
        // Directly call the controller method
        ResponseEntity<String> response = authController.registerUser(userDTO);

        // --- THEN ---
        // Assert on the returned ResponseEntity
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully: " + userAuthDTO.toString(), response.getBody());
    }

    @Test
    @DisplayName("POST /register - Email Already Taken")
    void registerUser_whenEmailExists_shouldReturnBadRequest() {
        // --- GIVEN ---
        // Mocking that the user already exists in the repository
        given(userRepository.findByEmail(userDTO.getEmail())).willReturn(Optional.of(existingUser));

        // --- WHEN ---
        ResponseEntity<String> response = authController.registerUser(userDTO);

        // --- THEN ---
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is already taken", response.getBody());
    }

    @Test
    @DisplayName("POST /login - Success")
    void login_whenCredentialsAreValid_shouldReturnOkAndToken() {
        // --- GIVEN ---
        // Mocking the authentication service to return a successful auth DTO with a token
        when(authService.authenticate(any(UserLoginDTO.class))).thenReturn(userAuthDTO);

        // --- WHEN ---
        ResponseEntity<?> response = authController.login(userLoginDTO);

        // --- THEN ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userAuthDTO.getToken(), response.getBody());
    }
}