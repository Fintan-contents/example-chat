package com.example.domain.model.account;

public class TemporaryAccount {

    private final TemporaryUserToken userToken;

    private final UserName userName;

    private final MailAddress mailAddress;

    private final HashedPassword password;

    public TemporaryAccount(TemporaryUserToken userToken, UserName userName, MailAddress mailAddress,
            HashedPassword password) {
        this.userToken = userToken;
        this.userName = userName;
        this.mailAddress = mailAddress;
        this.password = password;
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
}
