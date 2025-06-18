package com.sbaldasso.mybank.services;

import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.enums.Role;
import com.sbaldasso.mybank.auth.exception.UserNotFoundException;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import com.sbaldasso.mybank.auth.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserService.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder bCryptPasswordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        // Common setup for user and userDTO objects
        user = new User();
        user.setId(1L);
        user.setName("Original Name");
        user.setUsername("originaluser");
        user.setEmail("original@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(Role.USER));

        userDTO = UserDTO.builder()
                .name("Updated Name")
                .username("updateduser")
                .email("updated@example.com")
                .password("newPassword123")
                .roles(Set.of(Role.ADMIN))
                .build();
    }

    @Test
    @DisplayName("update should succeed when user exists")
    void update_whenUserExists_shouldReturnUpdatedUser() {
        // --- GIVEN ---
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.encode("newPassword123")).willReturn("newEncodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // --- WHEN ---
        User updatedUser = userService.update(userDTO, 1L);

        // --- THEN ---
        verify(userRepository).findById(1L);
        verify(bCryptPasswordEncoder).encode("newPassword123");
        verify(userRepository).save(any(User.class));

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getUsername()).isEqualTo("updateduser");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getPassword()).isEqualTo("newEncodedPassword");
        assertThat(updatedUser.getRoles()).contains(Role.ADMIN);
    }

    @Test
    @DisplayName("update should throw UserNotFoundException when user does not exist")
    void update_whenUserDoesNotExist_shouldThrowException() {
        // --- GIVEN ---
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // --- WHEN & THEN ---
        assertThrows(UserNotFoundException.class, () -> {
            userService.update(userDTO, 99L);
        });

        // Verify that save was never called
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteById should call repository delete method")
    void deleteById_shouldSucceed() {
        // --- GIVEN ---
        Long userId = 1L;
        // Mock the void method to do nothing when called
        doNothing().when(userRepository).deleteById(userId);

        // --- WHEN ---
        userService.deleteById(userId);

        // --- THEN ---
        // Verify that the deleteById method was called on the repository with the correct ID
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("getCurrentUser should return user when authenticated")
    void getCurrentUser_whenAuthenticated_shouldReturnUser() {
        // --- GIVEN ---
        String username = "test@example.com";
        // Mock the SecurityContextHolder to simulate an authenticated user
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        given(userRepository.findByEmail(username)).willReturn(Optional.of(user));

        // --- WHEN ---
        User currentUser = userService.getCurrentUser();

        // --- THEN ---
        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("getCurrentUser should throw exception when user not found in repository")
    void getCurrentUser_whenUserNotFound_shouldThrowException() {
        // --- GIVEN ---
        String username = "unknown@example.com";
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        given(userRepository.findByEmail(username)).willReturn(Optional.empty());

        // --- WHEN & THEN ---
        assertThrows(RuntimeException.class, () -> {
            userService.getCurrentUser();
        });
    }
}
