package com.example.domain.model.account;

import java.util.Collections;
import java.util.List;

public class Accounts {

    private final List<Account> accounts;

    public Accounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Account> asList() {
        return Collections.unmodifiableList(accounts);
    }
}
