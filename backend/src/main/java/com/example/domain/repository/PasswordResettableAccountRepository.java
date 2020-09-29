package com.example.domain.repository;

import com.example.domain.model.account.PasswordResettableAccount;
import com.example.domain.model.account.TemporaryUserToken;

public interface PasswordResettableAccountRepository {

    void add(PasswordResettableAccount resettableAccount);

    void remove(TemporaryUserToken userToken);

    PasswordResettableAccount findBy(TemporaryUserToken userToken);

}
