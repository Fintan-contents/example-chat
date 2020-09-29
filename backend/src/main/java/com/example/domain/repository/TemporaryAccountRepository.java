package com.example.domain.repository;

import com.example.domain.model.account.TemporaryAccount;
import com.example.domain.model.account.TemporaryUserToken;

public interface TemporaryAccountRepository {

    void add(TemporaryAccount temporaryAccount);

    void remove(TemporaryUserToken userToken);

    TemporaryAccount findBy(TemporaryUserToken userToken);

}
