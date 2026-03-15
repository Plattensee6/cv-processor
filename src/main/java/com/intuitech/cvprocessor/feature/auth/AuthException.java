package com.intuitech.cvprocessor.feature.auth;

/**
 * Thrown when an auth operation fails (e.g. invalid or expired reset token).
 */
public class AuthException extends Exception {

    public AuthException(String message) {
        super(message);
    }
}
