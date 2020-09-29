package com.example.presentation.websocket;

import java.io.IOException;

import javax.websocket.Session;

import com.example.domain.model.AccountId;
import com.example.domain.model.Payload;
import com.example.system.nablarch.handler.Notifier;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

public class WebSocketNotifier implements Notifier {

    private static final Logger LOGGER = LoggerManager.get(WebSocketNotifier.class);

    private final Session session;
    private final AccountId accountId;

    public WebSocketNotifier(Session session, AccountId accountId) {
        this.session = session;
        this.accountId = accountId;
    }

    @Override
    public void notify(Payload payload) {
        try {
            session.getBasicRemote().sendText(payload.value());
        } catch (IOException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.logWarn("Notify failure", e);
            }
        }
    }

    public AccountId accountId() {
        return accountId;
    }
}
