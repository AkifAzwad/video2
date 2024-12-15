package com.example.video2.controller;

import com.example.video2.service.UserService;
import com.example.video2.service.dto.LoginDto;
import com.example.video2.service.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @PostMapping("/register")
    public String register(@Valid @RequestBody UserDto user) {
        return service.registerUser(user);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginDto user) {
        return service.authenticateUser(user);
    }
}
