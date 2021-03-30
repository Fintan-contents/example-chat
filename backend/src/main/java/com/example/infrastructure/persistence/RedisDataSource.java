package com.example.infrastructure.persistence;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.example.domain.model.account.HashedPassword;
import com.example.domain.model.account.MailAddress;
import com.example.domain.model.account.TemporaryAccount;
import com.example.domain.model.account.TemporaryUserToken;
import com.example.domain.model.account.UserName;
import com.example.domain.repository.TemporaryAccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nablarch.core.repository.di.config.externalize.annotation.ComponentRef;
import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.integration.redisstore.lettuce.LettuceRedisClient;

@SystemRepositoryComponent
public class RedisDataSource implements TemporaryAccountRepository {

    private final LettuceRedisClient redisClient;

    private final long expireMilliseconds;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisDataSource(@ComponentRef("lettuceRedisClientProvider") LettuceRedisClient redisClient,
                           @ConfigValue("${signup.verification.expire}") String expireSeconds) {
        this.redisClient = redisClient;
        this.expireMilliseconds = TimeUnit.SECONDS.toMillis(Long.parseLong(expireSeconds));
    }

    @Override
    public void add(TemporaryAccount temporaryAccount) {
        String key = temporaryAccount.temporaryUserToken().value();
        byte[] jsonString = serialize(temporaryAccount);

        redisClient.set(key, jsonString);
        redisClient.pexpire(key, expireMilliseconds);
    }

    @Override
    public TemporaryAccount findBy(TemporaryUserToken userToken) {
        String key = userToken.value();
        if (!redisClient.exists(key)) {
            return null;
        }
        return deserialize(userToken, redisClient.get(key));
    }

    @Override
    public void remove(TemporaryUserToken userToken) {
        redisClient.del(userToken.value());
    }

    private byte[] serialize(TemporaryAccount temporaryAccount) {
        try {
            TemporaryAccountValue temporaryAccountValue = new TemporaryAccountValue();
            temporaryAccountValue.userName = temporaryAccount.userName().value();
            temporaryAccountValue.mailAddress = temporaryAccount.mailAddress().value();
            temporaryAccountValue.password = temporaryAccount.password().value();

            return objectMapper.writeValueAsBytes(temporaryAccountValue);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private TemporaryAccount deserialize(TemporaryUserToken userToken, byte[] jsonBytes) {
        try {
            TemporaryAccountValue temporaryAccountValue = objectMapper.readValue(jsonBytes,
                    TemporaryAccountValue.class);

            UserName userName = new UserName(temporaryAccountValue.userName);
            MailAddress mailAddress = new MailAddress(temporaryAccountValue.mailAddress);
            HashedPassword password = new HashedPassword(temporaryAccountValue.password);

            return new TemporaryAccount(userToken, userName, mailAddress, password);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class TemporaryAccountValue {

        public String userName;

        public String mailAddress;

        public String password;
    }
}
