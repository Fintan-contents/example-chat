package com.example.infrastructure.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "two_factor_authentication_setting")
@Access(AccessType.FIELD)
public class TwoFactorAuthenticationSettingEntity {

    @Id
    private Long accountId;

    private String status;

    private byte[] secretKey;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setSecretKey(byte[] secretKey) {
        this.secretKey = secretKey;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }
}
