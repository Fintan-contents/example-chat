package com.example.system.nablarch.handler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.domain.model.AccountId;
import com.example.domain.model.Payload;

public class NotifierManagerTest {

    @Test
    void アカウントにNotifierを関連付ける() throws Exception {
        AccountId accountId = new AccountId(UUID.randomUUID().toString());
        MockNotifier notifier1 = new MockNotifier();

        NotifierManager sut = new NotifierManager();
        sut.add(accountId, notifier1);

        Payload payload = new Payload(UUID.randomUUID().toString());
        sut.find(accountId).notify(payload);
        assertEquals(payload, notifier1.payload);
    }

    @Test
    void アカウントには複数のNotifierを関連付けられる() throws Exception {
        AccountId accountId = new AccountId(UUID.randomUUID().toString());
        MockNotifier notifier1 = new MockNotifier();
        MockNotifier notifier2 = new MockNotifier();

        NotifierManager sut = new NotifierManager();
        sut.add(accountId, notifier1);
        sut.add(accountId, notifier2);

        Payload payload = new Payload(UUID.randomUUID().toString());
        sut.find(accountId).notify(payload);
        assertEquals(payload, notifier1.payload);
        assertEquals(payload, notifier2.payload);
    }

    @Test
    void findはnullを返さない() throws Exception {
        AccountId accountId = new AccountId(UUID.randomUUID().toString());

        NotifierManager sut = new NotifierManager();

        Notifier notifier = sut.find(accountId);
        assertNotNull(notifier);
    }

    static class MockNotifier implements Notifier {

        Payload payload;

        @Override
        public void notify(Payload payload) {
            this.payload = payload;
        }
    }
}
