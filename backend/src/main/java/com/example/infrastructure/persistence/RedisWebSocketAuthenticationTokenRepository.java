package com.example.infrastructure.persistence;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.authentication.WebSocketAuthenticationToken;
import com.example.domain.repository.WebSocketAuthenticationTokenRepository;

import nablarch.core.repository.di.config.externalize.annotation.ComponentRef;
import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.integration.redisstore.lettuce.LettuceRedisClient;

@SystemRepositoryComponent
public class RedisWebSocketAuthenticationTokenRepository implements WebSocketAuthenticationTokenRepository {

    private final LettuceRedisClient redisClient;

    private final int timeoutMillis;

    public RedisWebSocketAuthenticationTokenRepository(@ComponentRef("lettuceRedisClientProvider") LettuceRedisClient redisClient,
            @ConfigValue("${websocket.onetimetoken.timeout}") String timeout) {
        this.redisClient = redisClient;
        this.timeoutMillis = (int) TimeUnit.SECONDS.toMillis(Long.parseLong(timeout));
    }

    @Override
    public void save(WebSocketAuthenticationToken webSocketAuthenticationToken, AccountId accountId) {
        String key = webSocketAuthenticationToken.value();
        redisClient.set(key, accountId.value().toString().getBytes(StandardCharsets.UTF_8));
        redisClient.pexpire(key, timeoutMillis);
    }
}
