package com.example.domain.model.channel;

import com.example.domain.model.account.AccountId;

public class ChannelMember {

    private final AccountId accountId;

    public ChannelMember(AccountId accountId) {
        this.accountId = accountId;
    }

    public AccountId accountId() {
        return accountId;
    }
}
