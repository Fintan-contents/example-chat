package com.example.presentation.websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.example.application.service.AuthenticationService;
import com.example.domain.model.AccountId;
import com.example.domain.model.AuthenticationToken;
import com.example.domain.model.Payload;
import com.example.system.nablarch.handler.NotifierManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.repository.disposal.Disposable;

@SystemRepositoryComponent
@ServerEndpoint(value = "/notification", configurator = SystemRepositoryWebSocketConfigurator.class)
public class WebSocketEndpoint implements Disposable {

    private static final Logger LOGGER = LoggerManager.get(WebSocketEndpoint.class);

    private final Payload pong = new Payload("{\"type\":\"pong\"}");
    private final Map<Session, WebSocketNotifier> notifiers = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AuthenticationService authenticationService;
    private final NotifierManager notifierManager;
    private final long maxIdleTimeoutSeconds;

    public WebSocketEndpoint(AuthenticationService authenticationService, NotifierManager notifierManager,
            @ConfigValue("${websocket.maxIdleTimeoutSeconds}") long maxIdleTimeoutSeconds) {
        this.authenticationService = authenticationService;
        this.notifierManager = notifierManager;
        this.maxIdleTimeoutSeconds = maxIdleTimeoutSeconds;
        // 開発時はmvn jetty:runで動かしているが、その場合にonOpenメソッドではスレッドコンテキストクラスローダーに
        // MavenのClassRealmがセットされ、WebSocketNotifierクラスのローディングでエラーが発生する場合がある。
        // (static finalなロガー変数の初期化でエラーになる)
        // workaroundにはなるが、WebSocketEndpointのコンストラクタでWebSocketNotifierクラスのローディングを
        // 済ませておく。
        new WebSocketNotifier(null, null);
    }

    @Override
    public void dispose() throws Exception {
        for (Session session : notifiers.keySet()) {
            session.close();
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug("WebSocket client connected: session = " + session);
        }
        List<String> tokenParameters = session.getRequestParameterMap().get("token");
        if (tokenParameters == null || tokenParameters.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.logDebug("Authentication token is missing: session = " + session);
            }
            closeAuthenticationFailedSession(session);
            return;
        }
        AuthenticationToken token = new AuthenticationToken(tokenParameters.get(0));
        AccountId accountId = authenticationService.authenticate(token);
        if (accountId == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.logDebug("AccountId is missing associated with authentication token: session = " + session);
            }
            closeAuthenticationFailedSession(session);
            return;
        }
        WebSocketNotifier notifier = new WebSocketNotifier(session, accountId);
        notifierManager.add(accountId, notifier);
        notifiers.put(session, notifier);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug("Connected: session = " + session + ", accountId = " + accountId);
        }
        session.setMaxIdleTimeout(TimeUnit.SECONDS.toMillis(maxIdleTimeoutSeconds));
        if (LOGGER.isDebugEnabled()) {
            LOGGER
                    .logDebug("Set max idle timeout to " + maxIdleTimeoutSeconds + " seconds: session = " + session
                            + ", accountId = " + accountId);
        }
    }

    private static void closeAuthenticationFailedSession(Session session) throws IOException {
        session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Authentication failed"));
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JsonNode root;
        try {
            root = objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.logWarn("Invalid JSON", e);
            }
            return;
        }
        JsonNode typeNode = root.get("type");
        if (typeNode != null && typeNode.isTextual() && Objects.equals(typeNode.asText(), "ping")) {
            WebSocketNotifier notifier = notifiers.get(session);
            notifier.notify(pong);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        if (!notifiers.containsKey(session)) {
            // 認証トークンが不正である等の理由により、管理対象になっていないセッションがクローズされた場合は何もしない
            return;
        }
        WebSocketNotifier notifier = notifiers.remove(session);
        AccountId accountId = notifier.accountId();
        notifierManager.remove(accountId, notifier);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug("Closed: session = " + session + ", accountId = " + accountId);
        }
    }
}
