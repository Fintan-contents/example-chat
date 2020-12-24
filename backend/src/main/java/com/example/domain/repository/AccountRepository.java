package com.example.domain.repository;

import com.example.domain.model.account.*;

public interface AccountRepository {

    void add(Account account);

    void remove(AccountId accountId);

    Account findById(AccountId accountId);

    Accounts findBy(AccountId... accountIds);

    Account findBy(MailAddress mailAddress);

    boolean existsBy(AccountId accountId);

    boolean existsBy(UserName userName);

    boolean existsBy(MailAddress mailAddress);
}
