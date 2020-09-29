package com.example.infrastructure.persistence;

import com.example.domain.model.account.*;
import com.example.domain.repository.AccountPasswordRepository;
import com.example.infrastructure.persistence.entity.PasswordEntity;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;

@SystemRepositoryComponent
public class AccountPasswordDataSource implements AccountPasswordRepository {

    @Override
    public void add(AccountPassword account) {
        PasswordEntity passwordEntity = new PasswordEntity();
        passwordEntity.setAccountId(account.accountId().value());
        passwordEntity.setPassword(account.password().value());
        UniversalDao.insert(passwordEntity);
    }

    @Override
    public void update(AccountPassword account) {
        PasswordEntity passwordEntity = new PasswordEntity();
        passwordEntity.setAccountId(account.accountId().value());
        passwordEntity.setPassword(account.password().value());
        UniversalDao.update(passwordEntity);
    }

    @Override
    public void remove(AccountId accountId) {
        PasswordEntity passwordEntity = new PasswordEntity();
        passwordEntity.setAccountId(accountId.value());

        UniversalDao.delete(passwordEntity);
    }

    @Override
    public AccountPassword findById(AccountId accountId) {
        try {
            PasswordEntity passwordEntity = UniversalDao.findById(PasswordEntity.class, accountId.value());
            return new AccountPassword(accountId, new HashedPassword(passwordEntity.getPassword()));
        } catch (NoDataException e) {
            return null;
        }
    }
}
