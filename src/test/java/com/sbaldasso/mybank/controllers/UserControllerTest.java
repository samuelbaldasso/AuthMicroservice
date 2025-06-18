package com.sbaldasso.mybank.controllers;

import com.sbaldasso.mybank.auth.controllers.UserController;
import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pure Unit tests for the UserController using only Mockito.
 */
public class UserControllerTest {

    // Mock the dependency of UserController
    @Mock
    private UserService userService;

    // The class under test. The mock will be injected into this instance.
    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        // Initializes mocks and injects them into userController
        MockitoAnnotations.openMocks(this);

        // Setup common test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        userDTO = UserDTO.builder()
                .email("updated@example.com")
                .password("newpassword123")
                .name("Updated User")
                .build();
    }

    @Test
    @DisplayName("GET /me - Success")
    void getCurrentUser_shouldReturnCurrentUser() {
        // --- GIVEN ---
        // Mock the service to return a user
        when(userService.getCurrentUser()).thenReturn(testUser);

        // --- WHEN ---
        // Directly call the controller method
        ResponseEntity<User> response = userController.getCurrentUser();

        // --- THEN ---
        // Assert on the returned ResponseEntity
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("PUT /update/{id} - Success")
    void updateUser_shouldReturnUpdatedUser() {
        // --- GIVEN ---
        // Mock the service to return the updated user when called with any DTO and the specific ID
        when(userService.update(any(UserDTO.class), eq(1L))).thenReturn(testUser);

        // --- WHEN ---
        ResponseEntity<User> response = userController.updateUser(userDTO, 1L);

        // --- THEN ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).update(any(UserDTO.class), eq(1L));
    }

    @Test
    @DisplayName("DELETE /delete/{id} - Success")
    void deleteUser_shouldReturnNoContent() {
        // --- GIVEN ---
        // The deleteById method is void, so we don't need to mock a return value.
        // `doNothing()` is useful for void methods to ensure the mock doesn't throw an exception.
        doNothing().when(userService).deleteById(1L);

        // --- WHEN ---
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // --- THEN ---
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // Verify that the service's deleteById method was called exactly once with the correct ID
        verify(userService, times(1)).deleteById(1L);
    }
}
