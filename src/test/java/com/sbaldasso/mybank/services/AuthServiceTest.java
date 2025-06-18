package com.sbaldasso.mybank.services;

import com.sbaldasso.mybank.auth.dto.UserAuthDTO;
import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.dto.UserLoginDTO;
import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.enums.Role;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import com.sbaldasso.mybank.auth.security.jwt.JWTTokenGenerator;
import com.sbaldasso.mybank.auth.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication; // <-- Import Authentication
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*; // <-- Import Mockito static methods

/**
 * Unit tests for the AuthService.
 *
 * @ExtendWith(MockitoExtension.class) is used to initialize mocks and inject them.
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    // Mocking the dependencies of AuthService
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTTokenGenerator jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    // Injects the mocks into the AuthService instance
    @InjectMocks
    private AuthService authService;

    private UserDTO userDTO;
    private UserLoginDTO userLoginDTO;
    private User user;

    @BeforeEach
    void setUp() {
        // Setup common test data
        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();

        userLoginDTO = UserLoginDTO.builder()
                .email("test@example.com")
                .password("password123").build();

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(Set.of(Role.USER, Role.ADMIN))
                .build();
    }

    @Test
    @DisplayName("register should save user and return UserAuthDTO with token")
    void register_shouldSucceed() {
        // --- GIVEN ---
        given(passwordEncoder.encode(userDTO.getPassword())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);
        // FIX: Use any(User.class) because the User object passed to generateToken in the service
        // is a new instance and won't match the 'user' instance in this test.
        given(jwtUtil.generateToken(any(User.class))).willReturn("dummy.jwt.token");

        // --- WHEN ---
        UserAuthDTO result = authService.register(userDTO);

        // --- THEN ---
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(User.class));

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("dummy.jwt.token");
    }

    @Test
    @DisplayName("authenticate should return UserAuthDTO on successful authentication")
    void authenticate_shouldSucceed() {
        // --- GIVEN ---
        // FIX: authenticationManager.authenticate is NOT a void method. It returns an Authentication object.
        // We mock it to return a dummy Authentication object to simulate success.
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(mock(Authentication.class));
        given(userRepository.findByEmail(userLoginDTO.getEmail())).willReturn(Optional.of(user));
        given(jwtUtil.generateToken(user)).willReturn("dummy.jwt.token");

        // --- WHEN ---
        UserAuthDTO result = authService.authenticate(userLoginDTO);

        // --- THEN ---
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword())
        );
        verify(userRepository).findByEmail("test@example.com");
        verify(jwtUtil).generateToken(user);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("dummy.jwt.token");
    }

    @Test
    @DisplayName("authenticate should throw exception when user is not found after successful auth")
    void authenticate_whenUserNotFound_shouldThrowException() {
        // --- GIVEN ---
        // FIX: Same as the above test, mock authenticate() to return an object.
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(mock(Authentication.class));
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // --- WHEN & THEN ---
        assertThrows(NoSuchElementException.class, () -> {
            authService.authenticate(userLoginDTO);
        });

        verify(jwtUtil, never()).generateToken(any());
    }
}