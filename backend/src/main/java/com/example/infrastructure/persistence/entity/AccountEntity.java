package com.example.infrastructure.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "account")
@Access(AccessType.FIELD)
public class AccountEntity {

    @Id
    private Long accountId;

    private String mailAddress;

    private String userName;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
