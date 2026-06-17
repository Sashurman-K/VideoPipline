package com.sashurman.userservice.dto;

public record RegisterRequest(
        String email,
        String password,
        String username
) {
}
