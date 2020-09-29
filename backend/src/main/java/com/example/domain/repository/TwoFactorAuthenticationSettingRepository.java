package com.example.domain.repository;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.account.TwoFactorAuthenticationSetting;

public interface TwoFactorAuthenticationSettingRepository {

    void add(TwoFactorAuthenticationSetting setting);

    void remove(AccountId accountId);

    TwoFactorAuthenticationSetting findById(AccountId accountId);
}
