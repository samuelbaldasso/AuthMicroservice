package com.sbaldasso.mybank.auth.services;

import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.exception.UserNotFoundException;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    private PasswordEncoder bCryptPasswordEncoder;

    public User create(UserDTO userDTO){
        User user = User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .username(userDTO.getUsername())
                .build();

        return userRepository.save(user);
    }

    public User update(UserDTO userDTO, Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setName(userDTO.getName());
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        return userRepository.save(user);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public void deleteById(Long id){
        userRepository.deleteById(id);
    }
}
