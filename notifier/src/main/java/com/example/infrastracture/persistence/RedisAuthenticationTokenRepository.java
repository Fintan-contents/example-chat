package com.example.infrastracture.persistence;

import com.example.domain.model.AccountId;
import com.example.domain.model.AuthenticationToken;
import com.example.domain.repository.AuthenticationTokenRepository;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.repository.disposal.Disposable;
import nablarch.core.repository.initialization.Initializable;

@SystemRepositoryComponent(name = "redisAuthenticationTokenRepository")
public class RedisAuthenticationTokenRepository implements AuthenticationTokenRepository, Initializable, Disposable {

    private final RedisClient redisClient;

    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;

    public RedisAuthenticationTokenRepository(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public void initialize() {
        connection = redisClient.connect();
        commands = connection.sync();
    }

    @Override
    public void dispose() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public AccountId authenticate(AuthenticationToken authenticationToken) {
        String value = commands.get(authenticationToken.value());
        if (value == null) {
            return null;
        }
        commands.del(authenticationToken.value());
        return new AccountId(value);
    }
}
