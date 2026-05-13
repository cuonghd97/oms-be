package com.semicolon.oms.auth.service;

import com.semicolon.oms.auth.dto.LoginRequest;
import com.semicolon.oms.auth.dto.RegisterRequest;
import com.semicolon.oms.auth.dto.TokenResponse;
import com.semicolon.oms.common.exception.DuplicateResourceException;
import com.semicolon.oms.common.security.JwtTokenProvider;
import com.semicolon.oms.user.dto.UserResponse;
import com.semicolon.oms.user.entity.Role;
import com.semicolon.oms.user.entity.User;
import com.semicolon.oms.user.entity.UserStatus;
import com.semicolon.oms.user.repository.RefreshTokenRepository;
import com.semicolon.oms.user.repository.RoleRepository;
import com.semicolon.oms.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private Role customerRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        customerRole = Role.builder().id(3L).name("CUSTOMER").build();
        testUser = User.builder()
                .id(1L).email("test@test.com").password("encoded").fullName("Test User")
                .status(UserStatus.ACTIVE).roles(Set.of(customerRole)).build();
    }

    @Test
    void register_shouldSucceed() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@test.com");
        request.setPassword("pass123");
        request.setFullName("New User");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse result = authService.register(request);
        assertThat(result.getEmail()).isEqualTo("new@test.com");
        assertThat(result.getRoles()).contains("CUSTOMER");
    }

    @Test
    void register_duplicateEmail_shouldThrow() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@test.com");
        request.setPassword("pass123");
        request.setFullName("User");

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void login_shouldReturnTokens() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("pass123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(tokenProvider.generateAccessToken(1L, "test@test.com")).thenReturn("access-token");
        when(tokenProvider.generateRefreshTokenValue()).thenReturn("refresh-token");
        when(tokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TokenResponse result = authService.login(request);
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
    }
}
