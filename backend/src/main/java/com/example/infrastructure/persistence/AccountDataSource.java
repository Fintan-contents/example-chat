package com.example.infrastructure.persistence;

import com.example.domain.model.account.*;
import com.example.infrastructure.persistence.entity.AccountEntity;
import com.example.domain.repository.AccountRepository;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.EntityList;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SystemRepositoryComponent
public class AccountDataSource implements AccountRepository {

    @Override
    public void add(Account account) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountId(account.accountId().value());
        accountEntity.setUserName(account.userName().value());
        accountEntity.setMailAddress(account.mailAddress().value());

        UniversalDao.insert(accountEntity);
    }

    @Override
    public void remove(AccountId accountId) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountId(accountId.value());

        UniversalDao.delete(accountEntity);
    }

    @Override
    public Account findById(AccountId accountId) {
        try {
            AccountEntity accountEntity = UniversalDao.findById(AccountEntity.class, accountId.value());
            return toAccount(accountEntity);
        } catch (NoDataException e) {
            return null;
        }
    }

    @Override
    public Accounts findBy(AccountId... accountIds) {
        Long[] ids = Arrays.stream(accountIds).map(AccountId::value).toArray(Long[]::new);
        EntityList<AccountEntity> accountEntities = UniversalDao.findAllBySqlFile(AccountEntity.class,
                "SELECT_BY_ACCOUNT_IDS", Map.of("accountIds", ids));

        List<Account> result = accountEntities.stream().map(this::toAccount).collect(Collectors.toList());
        return new Accounts(result);
    }

    @Override
    public Account findBy(MailAddress mailAddress) {
        Map<String, String> condition = Map.of("mailAddress", mailAddress.value());
        try {
            AccountEntity accountEntity = UniversalDao.findBySqlFile(AccountEntity.class, "SELECT_BY_MAIL_ADDRESS",
                    condition);
            return toAccount(accountEntity);

        } catch (NoDataException e) {
            return null;
        }
    }

    @Override
    public boolean existsBy(AccountId accountId) {
        try {
            UniversalDao.findById(AccountEntity.class, accountId.value());
            return true;
        } catch (NoDataException e) {
            return false;
        }
    }

    @Override
    public boolean existsBy(UserName userName) {
        return UniversalDao.exists(AccountEntity.class, "SELECT_FOR_EXISTS", Map.of("userName", userName.value()));
    }

    @Override
    public boolean existsBy(MailAddress mailAddress) {
        return UniversalDao.exists(AccountEntity.class, "SELECT_BY_MAIL_ADDRESS", Map.of("mailAddress", mailAddress.value()));
    }

    private Account toAccount(AccountEntity accountEntity) {
        AccountId id = new AccountId(accountEntity.getAccountId());
        UserName userName = new UserName(accountEntity.getUserName());
        MailAddress mailAddress = new MailAddress(accountEntity.getMailAddress());

        return new Account(id, userName, mailAddress);
    }
}
