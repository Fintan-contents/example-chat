package com.example.domain.model.account;

import java.util.concurrent.TimeUnit;

public class PasswordResettableAccount {

    private final TemporaryUserToken userToken;

    private final Account account;

    private final ExpireTime expireTime;

    public PasswordResettableAccount(TemporaryUserToken userToken, Account account) {
        this.userToken = userToken;
        this.account = account;
        this.expireTime = new ExpireTime(24, TimeUnit.HOURS);
    }

    public TemporaryUserToken userToken() {
        return userToken;
    }

    public Account account() {
        return account;
    }

    public ExpireTime expireTime() {
        return expireTime;
    }
}
