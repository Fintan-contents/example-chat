package com.example.domain.model.account;

public class TwoFactorAuthenticationSetting {

    private final AccountId id;

    private final TwoFactorAuthenticationSettingStatus twoFactorAuthenticationSettingStatus;

    private final SecretKey secretKey;

    public TwoFactorAuthenticationSetting(AccountId id,
            TwoFactorAuthenticationSettingStatus twoFactorAuthenticationSettingStatus, SecretKey secretKey) {
        this.id = id;
        this.twoFactorAuthenticationSettingStatus = twoFactorAuthenticationSettingStatus;
        this.secretKey = secretKey;
    }

    public static TwoFactorAuthenticationSetting createDefault(AccountId id) {
        return new TwoFactorAuthenticationSetting(id, TwoFactorAuthenticationSettingStatus.DISABLED, SecretKey.NONE);
    }

    public AccountId accountId() {
        return id;
    }

    public TwoFactorAuthenticationSettingStatus twoFactorAuthenticationSettingStatus() {
        return twoFactorAuthenticationSettingStatus;
    }

    public boolean isEnabled() {
        return twoFactorAuthenticationSettingStatus == TwoFactorAuthenticationSettingStatus.ENABLED;
    }

    public SecretKey secretKey() {
        return secretKey;
    }

    public TwoFactorAuthenticationSetting enable(SecretKey secretKey) {
        return new TwoFactorAuthenticationSetting(id, TwoFactorAuthenticationSettingStatus.ENABLED, secretKey);
    }

    public TwoFactorAuthenticationSetting disable() {
        return new TwoFactorAuthenticationSetting(id, TwoFactorAuthenticationSettingStatus.DISABLED, SecretKey.NONE);
    }
}
