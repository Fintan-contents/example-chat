package com.example.infrastructure.persistence;

import com.example.domain.model.account.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class RedisPasswordResettableAccountRepositoryTest {

    @Test
    public void 有効期限の設定値がミリ秒で設定されていること() {
        LettuceRedisClientMock mock = new LettuceRedisClientMock();
        RedisPasswordResettableAccountRepository sut = new RedisPasswordResettableAccountRepository(mock, "3");

        PasswordResettableAccount dummyAccount = new PasswordResettableAccount(
                new TemporaryUserToken("dummy"),
                new Account(new AccountId(123L), new UserName("dummy"), new MailAddress("dummy@example.com")));

        sut.add(dummyAccount);

        assertEquals(3000L, mock.getExpire());
    }
}
