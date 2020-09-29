package com.example.infrastructure.persistence;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.account.SecretKey;
import com.example.domain.model.account.TwoFactorAuthenticationSetting;
import com.example.domain.model.account.TwoFactorAuthenticationSettingStatus;
import com.example.infrastructure.persistence.entity.TwoFactorAuthenticationSettingEntity;
import com.example.domain.repository.TwoFactorAuthenticationSettingRepository;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;

@SystemRepositoryComponent
public class TwoFactorAuthenticationSettingDataSource implements TwoFactorAuthenticationSettingRepository {

    @Override
    public void add(TwoFactorAuthenticationSetting setting) {
        remove(setting.accountId());

        TwoFactorAuthenticationSettingEntity entity = new TwoFactorAuthenticationSettingEntity();
        entity.setAccountId(setting.accountId().value());
        entity.setStatus(setting.twoFactorAuthenticationSettingStatus().name());
        entity.setSecretKey(setting.secretKey().value());

        UniversalDao.insert(entity);
    }

    @Override
    public void remove(AccountId accountId) {
        TwoFactorAuthenticationSettingEntity entity = new TwoFactorAuthenticationSettingEntity();
        entity.setAccountId(accountId.value());

        UniversalDao.delete(entity);
    }

    @Override
    public TwoFactorAuthenticationSetting findById(AccountId accountId) {
        try {
            TwoFactorAuthenticationSettingEntity entity = UniversalDao
                    .findById(TwoFactorAuthenticationSettingEntity.class, accountId.value());
            return convert(entity);
        } catch (NoDataException e) {
            return null;
        }
    }

    private TwoFactorAuthenticationSetting convert(TwoFactorAuthenticationSettingEntity entity) {
        AccountId id = new AccountId(entity.getAccountId());
        TwoFactorAuthenticationSettingStatus twoFactorAuthenticationSettingStatus = TwoFactorAuthenticationSettingStatus
                .valueOf(entity.getStatus());
        SecretKey secretKey = new SecretKey(entity.getSecretKey());
        return new TwoFactorAuthenticationSetting(id, twoFactorAuthenticationSettingStatus, secretKey);
    }
}
