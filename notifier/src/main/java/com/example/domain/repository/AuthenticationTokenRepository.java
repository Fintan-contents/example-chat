package com.example.domain.repository;

import com.example.domain.model.AccountId;
import com.example.domain.model.AuthenticationToken;

public interface AuthenticationTokenRepository {

    AccountId authenticate(AuthenticationToken authenticationToken);
}
