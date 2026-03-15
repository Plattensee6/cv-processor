package com.intuitech.cvprocessor.feature.auth;

/**
 * Thrown when user registration fails (e.g. email already registered).
 */
public class RegistrationException extends Exception {

    public RegistrationException(String message) {
        super(message);
    }
}
