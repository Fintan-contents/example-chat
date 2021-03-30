package com.example.infrastructure.persistence;

import com.example.domain.model.account.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class RedisDataSourceTest {

    @Test
    public void 有効期限の設定値がミリ秒で設定されていること() {
        LettuceRedisClientMock mock = new LettuceRedisClientMock();
        RedisDataSource sut = new RedisDataSource(mock, "3");

        TemporaryAccount dummyAccount = new TemporaryAccount(
                new TemporaryUserToken("dummy"), new UserName("dummy"),
                new MailAddress("dummy@example.com"), new HashedPassword("dummy"));

        sut.add(dummyAccount);

        assertEquals(3000L, mock.getExpire());
    }
}
