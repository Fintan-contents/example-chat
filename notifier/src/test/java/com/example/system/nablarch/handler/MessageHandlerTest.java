package com.example.system.nablarch.handler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.domain.model.AccountId;
import com.example.domain.model.Destinations;
import com.example.domain.model.Payload;

import nablarch.fw.ExecutionContext;

public class MessageHandlerTest {

    @Test
    void handle() throws Exception {
        MockNotifier notifier1 = new MockNotifier();
        MockNotifier notifier2 = new MockNotifier();
        MockNotifier notifier3 = new MockNotifier();

        NotifierManager notifierManager = new NotifierManager();
        notifierManager.add(new AccountId("account1"), notifier1);
        notifierManager.add(new AccountId("account2"), notifier2);
        notifierManager.add(new AccountId("account3"), notifier3);

        MessageHandler sut = new MessageHandler(notifierManager);
        Message message = new Message();
        message.setRequestPath("foo");
        message.setDestinations(new Destinations(List.of(
                new AccountId("account1"),
                new AccountId("account2"),
                new AccountId("account4"))));
        message.setPayload(new Payload("bar"));
        sut.handle(message, new ExecutionContext());

        assertEquals("bar", notifier1.payload.value());
        assertEquals("bar", notifier2.payload.value());
        assertNull(notifier3.payload);
    }

    static class MockNotifier implements Notifier {

        Payload payload;

        @Override
        public void notify(Payload payload) {
            this.payload = payload;
        }
    }
}
