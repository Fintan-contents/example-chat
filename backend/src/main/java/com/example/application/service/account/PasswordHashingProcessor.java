package com.example.application.service.account;

import com.example.domain.model.account.HashedPassword;
import com.example.domain.model.account.RawPassword;

public interface PasswordHashingProcessor {

    HashedPassword hash(RawPassword rawPassword);

    boolean matches(HashedPassword hashedPassword, RawPassword rawPassword);
}
