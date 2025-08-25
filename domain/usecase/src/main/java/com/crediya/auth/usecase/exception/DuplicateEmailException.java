package com.crediya.auth.usecase.exception;

import lombok.Getter;

@Getter
public class DuplicateEmailException extends RuntimeException{
    private final String email;

    public DuplicateEmailException(String email) {
        super("email already exists: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
