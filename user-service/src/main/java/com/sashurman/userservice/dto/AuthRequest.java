package com.sashurman.userservice.dto;

public record AuthRequest(
        String email,
        String password
) {
}
