package com.example.domain.model.account;

public class AccountPassword {

    private final AccountId id;

    private final HashedPassword password;

    public AccountPassword(AccountId id, HashedPassword password) {
        this.id = id;
        this.password = password;
    }

    public AccountId accountId() {
        return id;
    }

    public HashedPassword password() {
        return password;
    }

    public AccountPassword change(HashedPassword newPassword) {
        return new AccountPassword(id, newPassword);
    }
}
