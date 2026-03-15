package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.api.CreateUserRequest;
import com.intuitech.cvprocessor.api.UpdateUserRequest;
import com.intuitech.cvprocessor.api.UserSummaryDTO;
import com.intuitech.cvprocessor.domain.auth.Role;
import com.intuitech.cvprocessor.domain.auth.User;
import com.intuitech.cvprocessor.feature.auth.repository.RoleRepository;
import com.intuitech.cvprocessor.feature.auth.repository.UserRepository;
import com.intuitech.cvprocessor.presentation.mapper.UserApiMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Administration", description = "Manage HR admin users")
public class UserAdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserSummaryDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(UserApiMapper::toSummaryDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<UserSummaryDTO> createUser(@RequestBody CreateUserRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        Set<Role> roles = roleRepository.findAll().stream()
                .filter(r -> request.getRoles() != null && request.getRoles().contains(r.getName()))
                .collect(Collectors.toSet());

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .locked(false)
                .failedLoginAttempts(0)
                .createdAt(now)
                .updatedAt(now)
                .roles(roles)
                .build();

        User saved = userRepository.save(user);
        return ResponseEntity.ok(UserApiMapper.toSummaryDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserSummaryDTO> updateUser(@PathVariable Long id,
                                                     @RequestBody UpdateUserRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        Set<Role> roles = roleRepository.findAll().stream()
                .filter(r -> request.getRoles() != null && request.getRoles().contains(r.getName()))
                .collect(Collectors.toSet());

        return userRepository.findById(id)
                .map(user -> {
                    User updated = User.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .passwordHash(user.getPasswordHash())
                            .fullName(request.getFullName())
                            .enabled(request.getEnabled() != null && request.getEnabled())
                            .locked(request.getLocked() != null && request.getLocked())
                            .failedLoginAttempts(user.getFailedLoginAttempts())
                            .lastLoginAt(user.getLastLoginAt())
                            .createdAt(user.getCreatedAt())
                            .roles(roles)
                            .passwordResetToken(user.getPasswordResetToken())
                            .passwordResetExpiresAt(user.getPasswordResetExpiresAt())
                            .updatedAt(now)
                            .build();
                    User saved = userRepository.save(updated);
                    return ResponseEntity.ok(UserApiMapper.toSummaryDto(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

