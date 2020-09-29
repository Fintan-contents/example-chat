package com.example.presentation.restapi.authentication;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.authentication.AuthenticationCode;
import com.example.presentation.restapi.authentication.TwoFactorAuthenticationAction.VerifyRequest;

public class VerifyRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private VerifyRequest sut = new VerifyRequest();

    @Test
    void valid() throws Exception {
        sut.code = new AuthenticationCode("123456");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_codeがnull() throws Exception {
        sut.code = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("code"));
    }
}
