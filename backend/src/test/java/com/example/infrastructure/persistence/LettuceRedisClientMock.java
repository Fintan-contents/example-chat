package com.example.infrastructure.persistence;

import nablarch.integration.redisstore.lettuce.LettuceRedisClient;

public class LettuceRedisClientMock implements LettuceRedisClient {

    private long expire;

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void set(String key, byte[] value) {

    }

    @Override
    public void pexpire(String key, long milliseconds) {
        this.expire = milliseconds;
    }

    @Override
    public void pexpireat(String key, long milliseconds) {

    }

    @Override
    public long pttl(String key) {
        return 0;
    }

    @Override
    public byte[] get(String key) {
        return new byte[0];
    }

    @Override
    public void del(String key) {

    }

    @Override
    public boolean exists(String key) {
        return false;
    }

    @Override
    public void dispose() {

    }

    public long getExpire() {
        return expire;
    }
}
