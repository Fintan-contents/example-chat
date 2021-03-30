package com.example.infrastructure.persistence;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.example.domain.model.account.Account;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.account.MailAddress;
import com.example.domain.model.account.PasswordResettableAccount;
import com.example.domain.model.account.TemporaryUserToken;
import com.example.domain.model.account.UserName;
import com.example.domain.repository.PasswordResettableAccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nablarch.core.repository.di.config.externalize.annotation.ComponentRef;
import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.integration.redisstore.lettuce.LettuceRedisClient;

@SystemRepositoryComponent
public class RedisPasswordResettableAccountRepository implements PasswordResettableAccountRepository {

    private final LettuceRedisClient redisClient;

    private final long expireMilliseconds;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisPasswordResettableAccountRepository(@ComponentRef("lettuceRedisClientProvider") LettuceRedisClient redisClient,
                                                    @ConfigValue("${passwordReset.verification.expire}") String expireSeconds) {
        this.redisClient = redisClient;
        this.expireMilliseconds = TimeUnit.SECONDS.toMillis(Long.parseLong(expireSeconds));
    }

    @Override
    public void add(PasswordResettableAccount resettableAccount) {
        String key = resettableAccount.userToken().value();
        byte[] value = serialize(resettableAccount.account());

        redisClient.set(key, value);
        redisClient.pexpire(key, expireMilliseconds);
    }

    @Override
    public PasswordResettableAccount findBy(TemporaryUserToken userToken) {
        String key = userToken.value();
        byte[] value = redisClient.get(key);

        if (value == null) {
            return null;
        }

        Account account = deserialize(value);
        return new PasswordResettableAccount(userToken, account);
    }

    @Override
    public void remove(TemporaryUserToken userToken) {
        redisClient.del(userToken.value());
    }

    private byte[] serialize(Account account) {
        try {
            Value value = new Value();
            value.accountId = account.accountId().value();
            value.userName = account.userName().value();
            value.mailAddress = account.mailAddress().value();

            return objectMapper.writeValueAsBytes(value);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private Account deserialize(byte[] jsonBytes) {
        try {
            Value value = objectMapper.readValue(jsonBytes, Value.class);

            AccountId accountId = new AccountId(value.accountId);
            UserName userName = new UserName(value.userName);
            MailAddress mailAddress = new MailAddress(value.mailAddress);

            return new Account(accountId, userName, mailAddress);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Value {

        public long accountId;

        public String userName;

        public String mailAddress;

        public String password;
    }
}
