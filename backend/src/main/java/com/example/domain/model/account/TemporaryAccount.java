package com.example.domain.model.account;

import java.util.concurrent.TimeUnit;

public class TemporaryAccount {

    private final TemporaryUserToken userToken;

    private final UserName userName;

    private final MailAddress mailAddress;

    private final HashedPassword password;

    private final ExpireTime expireTime;

    public TemporaryAccount(TemporaryUserToken userToken, UserName userName, MailAddress mailAddress,
            HashedPassword password) {
        this.userToken = userToken;
        this.userName = userName;
        this.mailAddress = mailAddress;
        this.password = password;
        this.expireTime = new ExpireTime(24, TimeUnit.HOURS);
    }

    public TemporaryUserToken temporaryUserToken() {
        return userToken;
    }

    public UserName userName() {
        return userName;
    }

    public MailAddress mailAddress() {
        return mailAddress;
    }

    public HashedPassword password() {
        return password;
    }

    public ExpireTime expireTime() {
        return expireTime;
    }
}
