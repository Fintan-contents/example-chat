package com.example.system.nablarch.handler;

import com.example.domain.model.AccountId;
import com.example.domain.model.Payload;

import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;

@SystemRepositoryComponent(name = "messageHandler")
public class MessageHandler implements Handler<Message, Void> {

    private final NotifierManager notifierManager;

    public MessageHandler(NotifierManager notifierManager) {
        this.notifierManager = notifierManager;
    }

    @Override
    public Void handle(Message message, ExecutionContext context) {
        Payload payload = message.getPayload();
        for (AccountId destination : message.getDestinations().asList()) {
            Notifier notifier = notifierManager.find(destination);
            notifier.notify(payload);
        }
        return null;
    }
}
