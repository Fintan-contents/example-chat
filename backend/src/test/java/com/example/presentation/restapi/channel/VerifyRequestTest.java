package com.example.presentation.restapi.channel;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.account.AccountId;
import com.example.presentation.restapi.channel.ChannelMembersAction.ChannelInviteRequest;

public class VerifyRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private ChannelInviteRequest sut = new ChannelInviteRequest();

    @Test
    void valid() throws Exception {
        sut.accountId = new AccountId(123L);

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_accountIdがnull() throws Exception {
        sut.accountId = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("accountId"));
    }
}
