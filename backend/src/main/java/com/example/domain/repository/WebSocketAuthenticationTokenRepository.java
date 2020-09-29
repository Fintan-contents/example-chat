package com.example.domain.repository;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.authentication.WebSocketAuthenticationToken;

public interface WebSocketAuthenticationTokenRepository {

    void save(WebSocketAuthenticationToken webSocketAuthenticationToken, AccountId accountId);
}
