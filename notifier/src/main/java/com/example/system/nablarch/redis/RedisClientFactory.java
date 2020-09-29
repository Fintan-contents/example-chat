package com.example.system.nablarch.redis;

import io.lettuce.core.RedisClient;
import nablarch.core.repository.di.ComponentFactory;

public class RedisClientFactory implements ComponentFactory<RedisClient> {

    private String uri;

    @Override
    public RedisClient createObject() {
        return RedisClient.create(uri);
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
