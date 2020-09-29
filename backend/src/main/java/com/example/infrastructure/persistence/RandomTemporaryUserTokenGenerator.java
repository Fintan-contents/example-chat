package com.example.infrastructure.persistence;

import com.example.domain.model.account.TemporaryUserToken;
import com.example.application.service.account.TemporaryUserTokenProvider;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

import java.util.UUID;

@SystemRepositoryComponent
public class RandomTemporaryUserTokenGenerator implements TemporaryUserTokenProvider {

    @Override
    public TemporaryUserToken generate() {
        return new TemporaryUserToken(UUID.randomUUID().toString());
    }
}
