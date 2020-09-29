package com.example.presentation.restapi.signup;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.account.MailAddress;
import com.example.domain.model.account.RawPassword;
import com.example.domain.model.account.UserName;
import com.example.presentation.restapi.signup.SignupAction.SignupRequest;

public class SignupRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private SignupRequest sut = new SignupRequest();

    @Test
    void valid() throws Exception {
        sut.userName = new UserName("testuser");
        sut.mailAddress = new MailAddress("test@example.com");
        sut.password = new RawPassword("pass123-");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_userNameがnull() throws Exception {
        sut.userName = null;
        sut.mailAddress = new MailAddress("test@example.com");
        sut.password = new RawPassword("pass123-");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("userName"));
    }

    @Test
    void invalid_mailAddressがnull() throws Exception {
        sut.userName = new UserName("testuser");
        sut.mailAddress = null;
        sut.password = new RawPassword("pass123-");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("mailAddress"));
    }

    @Test
    void invalid_passwordがnull() throws Exception {
        sut.userName = new UserName("testuser");
        sut.mailAddress = new MailAddress("test@example.com");
        sut.password = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("password"));
    }
}
