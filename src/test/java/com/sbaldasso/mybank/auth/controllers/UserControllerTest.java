package com.sbaldasso.mybank.auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void setup() {
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
    public void testGetCurrentUser() throws Exception {
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void testUpdateUser() throws Exception {
        Mockito.when(userService.update(any(UserDTO.class), eq(1L))).thenReturn(user);

        mockMvc.perform(put("/api/v1/users/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/users/delete/1"))
                .andExpect(status().isNoContent());
    }
}
