package com.example.infrastracture.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.domain.model.AccountId;
import com.example.domain.model.AuthenticationToken;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import nablarch.core.repository.di.ComponentDefinitionLoader;
import nablarch.core.repository.di.DiContainer;
import nablarch.core.repository.di.config.xml.XmlComponentDefinitionLoader;

public class RedisAuthenticationTokenRepositoryIT {

    private static String token = UUID.randomUUID().toString();

    private RedisClient redisClient;

    private RedisAuthenticationTokenRepository sut;

    private StatefulRedisConnection<String, String> connection;

    private RedisCommands<String, String> commands;

    @BeforeEach
    void init() {
        ComponentDefinitionLoader loader = new XmlComponentDefinitionLoader("classpath:env-importing.xml");
        DiContainer diContainer = new DiContainer(loader);
        String uri = diContainer.getComponentByName("nablarch.lettuce.simple.uri");

        redisClient = RedisClient.create(uri);
        connection = redisClient.connect();
        commands = connection.sync();
        commands.set(token, "account1");

        sut = new RedisAuthenticationTokenRepository(redisClient);
        sut.initialize();
    }

    @AfterEach
    void destroy() throws Exception {
        sut.dispose();
        connection.close();
    }

    @Test
    void 正しいトークンが渡された場合は認証に成功する() throws Exception {
        AccountId accountId = sut.authenticate(new AuthenticationToken(token));
        assertEquals("account1", accountId.value());
        //認証するとワンタイムトークンは削除される
        assertEquals(0L, commands.exists(token));
    }

    @Test
    void 不明なトークンが渡された場合は認証に失敗する() throws Exception {
        AccountId accountId = sut.authenticate(new AuthenticationToken("unknown token"));
        assertNull(accountId);
    }
}
