package com.sbaldasso.mybank.auth.controllers;

import com.sbaldasso.mybank.auth.dto.UserAuthDTO;
import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.dto.UserLoginDTO;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import com.sbaldasso.mybank.auth.services.AuthService;
import com.sbaldasso.mybank.auth.security.jwt.JWTTokenGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTTokenGenerator jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email is already taken", HttpStatus.BAD_REQUEST);
        }

        UserAuthDTO userAuthDTO = authService.register(user);
        return new ResponseEntity<>("User registered successfully: " + userAuthDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO user) {
        UserAuthDTO authentication = authService.authenticate(user);
        return ResponseEntity.ok(authentication.getToken());
    }

}
