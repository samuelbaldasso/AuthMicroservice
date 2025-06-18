package com.sbaldasso.mybank.services;

import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.enums.Role;
import com.sbaldasso.mybank.auth.exception.UserNotFoundException;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import com.sbaldasso.mybank.auth.services.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the CustomUserDetailsService.
 */
@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        // Setup a mock user object to be returned by the repository
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword123");
        user.setRoles(Set.of(Role.USER, Role.ADMIN));
    }

    @Test
    @DisplayName("loadUserByUsername should return UserDetails when user exists")
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // --- GIVEN ---
        // Mock the repository to return our test user when searched by email
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));

        // --- WHEN ---
        // Call the method under test
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // --- THEN ---
        // Verify that the repository method was called
        verify(userRepository).findByEmail("test@example.com");

        // Assert that the returned UserDetails object is not null and has the correct properties
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());

        // Verify that the roles were correctly converted to authorities
        Set<SimpleGrantedAuthority> expectedAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
        assertThat(userDetails.getAuthorities()).isEqualTo(expectedAuthorities);
    }

    @Test
    @DisplayName("loadUserByUsername should throw UserNotFoundException when user does not exist")
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowException() {
        // --- GIVEN ---
        // Mock the repository to return an empty Optional, simulating a user not found
        String nonExistentEmail = "unknown@example.com";
        given(userRepository.findByEmail(nonExistentEmail)).willReturn(Optional.empty());

        // --- WHEN & THEN ---
        // Assert that a UserNotFoundException is thrown when the method is called
        // Note: The UserDetailsService interface specifies UsernameNotFoundException,
        // but we test for the actual exception thrown by the implementation.
        assertThrows(UserNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(nonExistentEmail);
        });

        // Verify that the repository method was called
        verify(userRepository).findByEmail(nonExistentEmail);
    }
}