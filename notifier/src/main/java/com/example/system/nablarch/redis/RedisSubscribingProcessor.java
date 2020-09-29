package com.example.system.nablarch.redis;

import java.util.List;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.repository.disposal.Disposable;
import nablarch.core.repository.initialization.Initializable;

@SystemRepositoryComponent(name = "redisSubscribingProcessor")
public class RedisSubscribingProcessor implements Initializable, Disposable {

    private StatefulRedisPubSubConnection<String, String> connection;
    private RedisPubSubCommands<String, String> commands;

    private final RedisClient redisClient;
    private final RedisPubSubListener<String, String> listener;
    private final List<String> channels;

    public RedisSubscribingProcessor(RedisClient redisClient, RedisPubSubListener<String, String> listener,
            @ConfigValue("${redis.subscribe.channels}") String[] channels) {
        this.redisClient = redisClient;
        this.listener = listener;
        this.channels = List.of(channels);
    }

    @Override
    public void initialize() {
        connection = redisClient.connectPubSub();
        connection.addListener(listener);
        commands = connection.sync();
        commands.subscribe(channels.toArray(String[]::new));
    }

    @Override
    public void dispose() throws Exception {
        if (commands != null) {
            commands.unsubscribe(channels.toArray(String[]::new));
        }
        if (connection != null) {
            connection.close();
        }
    }
}
