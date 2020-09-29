package com.example.infrastructure.persistence;

import java.util.UUID;

import com.example.application.service.authentication.WebSocketAuthenticationTokenProvider;
import com.example.domain.model.authentication.WebSocketAuthenticationToken;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class UUIDWebSocketAuthenticationTokenGenerator implements WebSocketAuthenticationTokenProvider {

    @Override
    public WebSocketAuthenticationToken provide() {
        return new WebSocketAuthenticationToken(UUID.randomUUID().toString());
    }
}
