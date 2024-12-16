package com.example.video2.service;

import com.example.video2.model.Role;
import com.example.video2.model.User;
import com.example.video2.repository.UserRepository;
import com.example.video2.security.JwtUtil;
import com.example.video2.service.dto.LoginDto;
import com.example.video2.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(UserDto userDto) {
        userRepository.findByUsername(userDto.getUsername())
                .ifPresent(user -> {
                    throw new RuntimeException("Username already exists");
                });

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.valueOf(userDto.getRole().toUpperCase()));

        userRepository.save(user);
        return "User registered successfully";
    }

    public String authenticateUser(LoginDto loginDto) {
        return userRepository.findByUsername(loginDto.getUsername())
                .filter(user -> passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
                .map(jwtUtil::generateToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
    }
}
