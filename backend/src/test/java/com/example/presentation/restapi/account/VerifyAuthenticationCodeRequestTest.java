package com.example.presentation.restapi.account;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.authentication.AuthenticationCode;
import com.example.presentation.restapi.account.TwoFactorAuthenticationSettingAction.VerifyAuthenticationCodeRequest;

public class VerifyAuthenticationCodeRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private VerifyAuthenticationCodeRequest sut = new VerifyAuthenticationCodeRequest();

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
