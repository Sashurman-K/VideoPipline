package com.sashurman.userservice.service;

import com.sashurman.common.exception.DuplicateException;
import com.sashurman.common.exception.NotFoundException;
import com.sashurman.userservice.dto.AuthRequest;
import com.sashurman.userservice.dto.AuthResponse;
import com.sashurman.userservice.dto.RegisterRequest;
import com.sashurman.userservice.exception.InvalidCredentialsException;
import com.sashurman.userservice.model.User;
import com.sashurman.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User register(RegisterRequest request) {
        if (userRepository.existsUserByEmail(request.email())) {
            throw new DuplicateException(String.format("Email %s already registered", request.email()));
        }
        if (userRepository.existsUserByUsername(request.username())){
            throw new DuplicateException(String.format("Username %s already registered", request.username()));
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        return userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}