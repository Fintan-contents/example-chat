package com.example.presentation.restapi.account;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.account.MailAddress;
import com.example.presentation.restapi.account.PasswordResetAction.PasswordResetRequest;

public class PasswordResetRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private PasswordResetRequest sut = new PasswordResetRequest();

    @Test
    void valid() throws Exception {
        sut.mailAddress = new MailAddress("test@example.com");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_mailAddressがnull() throws Exception {
        sut.mailAddress = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("mailAddress"));
    }
}
