package com.example.infrastructure.service;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.authentication.CredentialNotFoundException;
import com.example.domain.model.account.AccountId;

import nablarch.core.ThreadContext;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ThreadContextLoginAccountIdSupplier implements LoginAccountIdSupplier {

    @Override
    public AccountId supply() {
        String userId = ThreadContext.getUserId();
        if (userId == null) {
            throw new CredentialNotFoundException("Must be authenticated.");
        }
        long value = Long.parseLong(userId);
        return new AccountId(value);
    }
}
