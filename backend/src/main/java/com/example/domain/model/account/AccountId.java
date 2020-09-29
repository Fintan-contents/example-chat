package com.example.domain.model.account;

import java.util.Objects;

public class AccountId {

    private final Long value;

    public AccountId(Long value) {
        Objects.requireNonNull(value);

        this.value = value;
    }

    public Long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AccountId accountId = (AccountId) o;
        return Objects.equals(value, accountId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
