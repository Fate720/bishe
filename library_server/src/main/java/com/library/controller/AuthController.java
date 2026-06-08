package com.library.controller;

import com.library.common.Result;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth management APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login with username and password, returns JWT token")
    public Result<Map<String, String>> login(@Validated @RequestBody LoginRequest login) {
        return Result.success(authService.login(login.getUsername(), login.getPassword()));
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user account")
    public Result<User> register(@Validated @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current logged-in user details and permissions")
    public Result<Map<String, Object>> getCurrentUser() {
        return Result.success(authService.getCurrentUser());
    }
}
