package com.example.infrastructure.persistence;

import com.example.infrastructure.persistence.entity.AccountIdSequence;
import com.example.domain.model.account.AccountId;
import com.example.application.service.account.AccountIdProvider;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.UniversalDao;

@SystemRepositoryComponent
public class SequentialAccountIdProvider implements AccountIdProvider {

    @Override
    public AccountId generate() {
        AccountIdSequence accountIdSequence = UniversalDao.findBySqlFile(AccountIdSequence.class, "NEXT_ACCOUNT_ID",
                new Object[0]);
        return new AccountId(accountIdSequence.getAccountId());
    }
}
