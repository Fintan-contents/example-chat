package com.example.infrastructure.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "password")
@Access(AccessType.FIELD)
public class PasswordEntity {

    @Id
    private Long accountId;

    private String password;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
