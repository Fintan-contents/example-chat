package com.example.domain.repository;

import com.example.domain.model.account.*;

public interface AccountRepository {

    void add(Account account);

    void remove(AccountId accountId);

    Account findById(AccountId accountId);

    Accounts findBy(AccountId... accountIds);

    Account findBy(MailAddress mailAddress);

    boolean existsBy(UserName userName);
}
