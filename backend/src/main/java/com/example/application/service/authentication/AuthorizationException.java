package com.example.application.service.authentication;

import com.example.application.ChatApplicationException;

public class AuthorizationException extends ChatApplicationException {

    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String message) {
        super(message);
    }
}
