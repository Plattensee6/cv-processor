package com.intuitech.cvprocessor.feature.auth;

import com.intuitech.cvprocessor.api.AuthResponse;
import com.intuitech.cvprocessor.domain.auth.Role;
import com.intuitech.cvprocessor.domain.auth.User;
import com.intuitech.cvprocessor.feature.auth.repository.RoleRepository;
import com.intuitech.cvprocessor.feature.auth.repository.UserRepository;
import com.intuitech.cvprocessor.techcore.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int PASSWORD_RESET_TOKEN_VALIDITY_HOURS = 24;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse authenticate(String email, String rawPassword) throws AuthenticationException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!user.isEnabled() || user.isLocked()) {
            throw new AuthenticationException("Account is disabled or locked");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateAccessToken(user);
        return new AuthResponse().accessToken(token).tokenType("Bearer");
    }

    public void register(String email, String fullName, String password) throws RegistrationException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RegistrationException("Email already registered");
        }
        Set<Role> roles = roleRepository.findByName("HR")
                .map(Set::of)
                .orElseGet(Set::of);
        OffsetDateTime now = OffsetDateTime.now();
        User user = User.builder()
                .email(email)
                .fullName(fullName)
                .passwordHash(passwordEncoder.encode(password))
                .enabled(true)
                .locked(false)
                .failedLoginAttempts(0)
                .createdAt(now)
                .updatedAt(now)
                .roles(roles)
                .build();
        userRepository.save(user);
    }

    public Optional<String> createPasswordResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        String token = UUID.randomUUID().toString().replace("-", "");
        OffsetDateTime expiresAt = OffsetDateTime.now().plusHours(PASSWORD_RESET_TOKEN_VALIDITY_HOURS);
        OffsetDateTime now = OffsetDateTime.now();
        User updated = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .fullName(user.getFullName())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .passwordResetToken(token)
                .passwordResetExpiresAt(expiresAt)
                .updatedAt(now)
                .build();
        userRepository.save(updated);
        return Optional.of(token);
    }

    public void resetPassword(String token, String newPassword) throws AuthException {
        OffsetDateTime now = OffsetDateTime.now();
        User user = userRepository
                .findByPasswordResetTokenAndPasswordResetExpiresAtAfter(token, now)
                .orElseThrow(() -> new AuthException("Invalid or expired reset token"));
        User updated = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(passwordEncoder.encode(newPassword))
                .fullName(user.getFullName())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .passwordResetToken(null)
                .passwordResetExpiresAt(null)
                .updatedAt(now)
                .build();
        userRepository.save(updated);
    }
}

