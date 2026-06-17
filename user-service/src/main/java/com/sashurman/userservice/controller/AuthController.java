package com.sashurman.userservice.controller;

import com.sashurman.userservice.dto.AuthRequest;
import com.sashurman.userservice.dto.AuthResponse;
import com.sashurman.userservice.dto.RegisterRequest;
import com.sashurman.userservice.model.User;
import com.sashurman.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/api/v1/users/me")
    public ResponseEntity<User> getMe(@RequestHeader("X-User-Id") String userId) {
        User user = userService.getUserById(UUID.fromString(userId));
        return ResponseEntity.ok(user);
    }
}