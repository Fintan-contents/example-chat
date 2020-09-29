package com.example.presentation.restapi.account;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.account.TemporaryUserToken;
import com.example.presentation.restapi.account.PasswordResetAction.VerifyTokenRequest;

public class VerifyTokenRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private VerifyTokenRequest sut = new VerifyTokenRequest();

    @Test
    void valid() throws Exception {
        sut.token = new TemporaryUserToken("testtoken");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_tokenがnull() throws Exception {
        sut.token = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("token"));
    }
}
