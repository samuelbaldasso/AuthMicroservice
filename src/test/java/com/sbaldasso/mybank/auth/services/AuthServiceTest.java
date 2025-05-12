package com.sbaldasso.mybank.auth.services;

import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.dto.UserAuthDTO;
import com.sbaldasso.mybank.auth.dto.UserLoginDTO;
import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private UserDTO userDTO;
    private UserLoginDTO userLoginDTO;
    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("password")
                .build();

        userLoginDTO = UserLoginDTO.builder()
                .email("test@example.com")
                .password("password")
                .build();

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
    }

    @Test
    public void testRegisterUser_Success() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserAuthDTO result = authService.register(userDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.register(userDTO);
        });

        String expectedMessage = "Email already in use";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testAuthenticate_Success() {
        when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.of(user));
        // Add more mocks as needed for password verification and token generation

        UserAuthDTO result = authService.authenticate(userLoginDTO);

        assertNotNull(result);
        // Add assertions for token or other fields as needed
    }
}
