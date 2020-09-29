package com.example.domain.model.account;

public class Account {

    private final AccountId id;

    private final UserName userName;

    private final MailAddress mailAddress;

    public Account(AccountId id, UserName userName, MailAddress mailAddress) {
        this.id = id;
        this.userName = userName;
        this.mailAddress = mailAddress;
    }

    public AccountId accountId() {
        return id;
    }

    public UserName userName() {
        return userName;
    }

    public MailAddress mailAddress() {
        return mailAddress;
    }
}
