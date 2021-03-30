package com.example.domain.model.account;

public class PasswordResettableAccount {

    private final TemporaryUserToken userToken;

    private final Account account;

    public PasswordResettableAccount(TemporaryUserToken userToken, Account account) {
        this.userToken = userToken;
        this.account = account;
    }

    public TemporaryUserToken userToken() {
        return userToken;
    }

    public Account account() {
        return account;
    }
}
