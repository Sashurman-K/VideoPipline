import com.sashurman.userservice.dto.AuthRequest;
import com.sashurman.userservice.dto.AuthResponse;
import com.sashurman.userservice.dto.RegisterRequest;
import com.sashurman.userservice.model.User;
import com.sashurman.userservice.repository.UserRepository;
import com.sashurman.userservice.service.JwtService;
import com.sashurman.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_Success(){
        RegisterRequest request = new RegisterRequest("test@test.com", "password", "test");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("hashed_password");

        User user = new User();
        user.setPasswordHash("hashed_password");
        user.setEmail(request.email());
        user.setId(UUID.randomUUID());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(request);
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        assertEquals("hashed_password", result.getPasswordHash());
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    void login_Success(){
        AuthRequest request = new AuthRequest("test@test.com", "password");
        User user = new User(UUID.randomUUID(), "test@test.com", "hashed_password");

        when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(true);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getId(), user.getEmail())).thenReturn("access_token");

        AuthResponse response = userService.login(request);
        assertNotNull(response);
        assertEquals("access_token", response.token());
    }
}
