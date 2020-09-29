package com.example.presentation.restapi.signup;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.account.TemporaryUserToken;
import com.example.presentation.restapi.signup.SignupAction.VerificationRequest;

public class VerificationRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private VerificationRequest sut = new VerificationRequest();

    @Test
    void valid() throws Exception {
        sut.userToken = new TemporaryUserToken("testtoken");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_userTokenがnull() throws Exception {
        sut.userToken = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("userToken"));
    }
}
