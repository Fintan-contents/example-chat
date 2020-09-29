package com.example.application.service.account;

import com.example.domain.model.account.AccountId;

public interface AccountIdProvider {

    AccountId generate();
}
