package com.example.presentation.restapi.account;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.account.RawPassword;
import com.example.domain.model.account.TemporaryUserToken;
import com.example.presentation.restapi.account.PasswordResetAction.NewPasswordRequest;

public class NewPasswordRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private NewPasswordRequest sut = new NewPasswordRequest();

    @Test
    void valid() throws Exception {
        sut.token = new TemporaryUserToken("testtoken");
        sut.newPassword = new RawPassword("pass123-");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_tokenがnull() throws Exception {
        sut.token = null;
        sut.newPassword = new RawPassword("pass123-");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("token"));
    }

    @Test
    void invalid_newPasswordがnull() throws Exception {
        sut.token = new TemporaryUserToken("testtoken");
        sut.newPassword = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("newPassword"));
    }
}
