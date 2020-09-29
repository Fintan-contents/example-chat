package com.example.application.service.account;

import com.example.domain.model.account.TemporaryUserToken;

public interface TemporaryUserTokenProvider {

    TemporaryUserToken generate();
}
