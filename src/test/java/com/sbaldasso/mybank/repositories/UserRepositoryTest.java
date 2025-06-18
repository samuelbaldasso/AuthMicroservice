package com.sbaldasso.mybank.repositories;

import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.exception.UserNotFoundException;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        User result = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        assertEquals(user, result);
        verify(userRepository, times(1)).findByEmail(email);
    }
}