package com.sbaldasso.mybank.auth.services;
import com.sbaldasso.mybank.auth.dto.UserAuthDTO;
import com.sbaldasso.mybank.auth.dto.UserDTO;
import com.sbaldasso.mybank.auth.dto.UserLoginDTO;
import com.sbaldasso.mybank.auth.entities.User;
import com.sbaldasso.mybank.auth.repositories.UserRepository;
import com.sbaldasso.mybank.security.jwt.JWTTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenGenerator jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserAuthDTO register(UserDTO request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        var jwtToken = jwtUtil.generateToken(user);
        return UserAuthDTO.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .build();
    }

    public UserAuthDTO authenticate(UserLoginDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtUtil.generateToken(user);
        return UserAuthDTO.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .build();
    }
}