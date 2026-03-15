package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.api.ErrorMessage;
import com.intuitech.cvprocessor.api.ForgotPasswordRequest;
import com.intuitech.cvprocessor.api.ForgotPasswordResponse;
import com.intuitech.cvprocessor.api.LoginRequest;
import com.intuitech.cvprocessor.api.MessageResponse;
import com.intuitech.cvprocessor.api.RegisterRequest;
import com.intuitech.cvprocessor.api.ResetPasswordRequest;
import com.intuitech.cvprocessor.feature.auth.AuthenticationException;
import com.intuitech.cvprocessor.feature.auth.AuthException;
import com.intuitech.cvprocessor.feature.auth.AuthService;
import com.intuitech.cvprocessor.feature.auth.RegistrationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication endpoints for HR and candidates")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Authentication attempt for email: {}", request.getEmail());
        try {
            return ResponseEntity.ok(authService.authenticate(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(401).body(new ErrorMessage().message("Invalid credentials"));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new HR user")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        try {
            authService.register(request.getEmail(), request.getFullName(), request.getPassword());
            return ResponseEntity.ok(new MessageResponse().message("Registration successful. You can now log in."));
        } catch (RegistrationException e) {
            log.warn("Registration failed for {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessage().message(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset; returns reset token for the reset page")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        var tokenOpt = authService.createPasswordResetToken(request.getEmail());
        String message = "If an account exists, you can use the link below to reset your password.";
        ForgotPasswordResponse res = new ForgotPasswordResponse();
        res.setMessage(message);
        res.setResetToken(tokenOpt.orElse(null));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using token from forgot-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new MessageResponse().message("Password has been reset. You can now log in."));
        } catch (AuthException e) {
            log.warn("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessage().message(e.getMessage()));
        }
    }
}

