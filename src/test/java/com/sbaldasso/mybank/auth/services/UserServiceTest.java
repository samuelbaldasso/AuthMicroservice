package com.sbaldasso.mybank.auth.services;

import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setName("Test User");
        user.setPassword("password");
        user.setRoles(new HashSet<>());

        userDTO = UserDTO.builder()
                .email("test@example.com")
                .username("testuser")
                .name("Test User")
                .password("password")
                .roles(new HashSet<>())
                .build();
    }

    @Test
    public void testGetCurrentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void testUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.update(userDTO, 1L);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
