package com.example.application.service.authentication;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.authentication.WebSocketAuthenticationToken;
import com.example.domain.repository.WebSocketAuthenticationTokenRepository;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class WebSocketAuthenticationService {

    private final LoginAccountIdSupplier loginAccountIdSupplier;
    private final WebSocketAuthenticationTokenProvider webSocketAuthenticationTokenProvider;
    private final WebSocketAuthenticationTokenRepository webSocketAuthenticationTokenRepository;

    public WebSocketAuthenticationService(LoginAccountIdSupplier loginAccountIdSupplier,
            WebSocketAuthenticationTokenProvider webSocketAuthenticationTokenProvider,
            WebSocketAuthenticationTokenRepository webSocketAuthenticationTokenRepository) {
        this.loginAccountIdSupplier = loginAccountIdSupplier;
        this.webSocketAuthenticationTokenProvider = webSocketAuthenticationTokenProvider;
        this.webSocketAuthenticationTokenRepository = webSocketAuthenticationTokenRepository;
    }

    public WebSocketAuthenticationToken prepareToken() {
        AccountId accountId = loginAccountIdSupplier.supply();
        WebSocketAuthenticationToken webSocketAuthenticationToken = webSocketAuthenticationTokenProvider.provide();
        webSocketAuthenticationTokenRepository.save(webSocketAuthenticationToken, accountId);
        return webSocketAuthenticationToken;
    }
}
