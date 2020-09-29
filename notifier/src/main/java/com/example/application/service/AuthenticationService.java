package com.example.application.service;

import com.example.domain.model.AccountId;
import com.example.domain.model.AuthenticationToken;
import com.example.domain.repository.AuthenticationTokenRepository;

import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class AuthenticationService {

    private final AuthenticationTokenRepository authenticationTokenRepository;

    public AuthenticationService(AuthenticationTokenRepository authenticationTokenRepository) {
        this.authenticationTokenRepository = authenticationTokenRepository;
    }

    public AccountId authenticate(AuthenticationToken authenticationToken) {
        return authenticationTokenRepository.authenticate(authenticationToken);
    }
}
