package com.example.application.service.authentication;

import com.example.application.ChatApplicationException;

public class CredentialNotFoundException extends ChatApplicationException {

    public CredentialNotFoundException() {
        super();
    }

    public CredentialNotFoundException(String message) {
        super(message);
    }
}
