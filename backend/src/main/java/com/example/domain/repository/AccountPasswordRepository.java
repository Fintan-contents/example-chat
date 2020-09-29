package com.example.domain.repository;

import com.example.domain.model.account.*;

public interface AccountPasswordRepository {

    void add(AccountPassword account);

    void update(AccountPassword account);

    void remove(AccountId accountId);

    AccountPassword findById(AccountId accountId);
}
