package com.example.infrastructure.service;

import com.example.application.service.account.PasswordHashingProcessor;
import com.example.domain.model.account.HashedPassword;
import com.example.domain.model.account.RawPassword;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PBKDF2PasswordHashingProcessorTest {

    @Test
    void hashAndMatches() {
        PasswordHashingProcessor sut = new PBKDF2PasswordHashingProcessor();
        RawPassword rawPassword = new RawPassword(UUID.randomUUID().toString());
        HashedPassword hashedPassword = sut.hash(rawPassword);
        boolean matched = sut.matches(hashedPassword, rawPassword);
        assertTrue(matched);
    }
}
