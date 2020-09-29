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
import com.example.presentation.restapi.account.MyAccountDeletionAction.AccountDeleteRequest;

public class AccountDeleteRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private AccountDeleteRequest sut = new AccountDeleteRequest();

    @Test
    void valid() throws Exception {
        sut.password = new RawPassword("pass123-");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_passwordがnull() throws Exception {
        sut.password = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("password"));
    }
}
