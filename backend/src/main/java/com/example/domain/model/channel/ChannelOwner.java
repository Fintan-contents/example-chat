package com.example.domain.model.channel;

import com.example.domain.model.account.AccountId;

public class ChannelOwner {

    private final AccountId accountId;

    public ChannelOwner(AccountId accountId) {
        this.accountId = accountId;
    }

    public AccountId accountId() {
        return accountId;
    }

}
