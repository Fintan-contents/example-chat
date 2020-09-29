package com.example.presentation.websocket;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer;
import org.eclipse.jetty.websocket.javax.server.internal.JavaxWebSocketServerContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.application.service.AuthenticationService;
import com.example.domain.model.AccountId;
import com.example.domain.model.AuthenticationToken;
import com.example.domain.repository.AuthenticationTokenRepository;
import com.example.system.nablarch.handler.NotifierManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nablarch.core.repository.SystemRepository;

public class WebSocketEndpointIT {

    private static final long TIMEOUT_VALUE = 100;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    private Server server;
    private WebSocketEndpoint sut;

    @BeforeEach
    void init() throws Exception {
        NotifierManager notifierManager = new NotifierManager();
        AuthenticationTokenRepository authenticationTokenRepository = new MockAuthenticationTokenRepository()
                .add("token1", "account1")
                .add("token2", "account2");
        AuthenticationService authenticationService = new AuthenticationService(
                authenticationTokenRepository);
        sut = new WebSocketEndpoint(authenticationService, notifierManager, 30);
        SystemRepository.load(() -> Map.of(WebSocketEndpoint.class.getName(), sut));

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setBaseResource(Resource.newResource("src/test/resources/war"));
        server = new Server(9081);
        server.setHandler(context);
        JavaxWebSocketServerContainer javaxWebSocketServerContainer = JavaxWebSocketServletContainerInitializer
                .initialize(context);
        javaxWebSocketServerContainer.addEndpoint(WebSocketEndpoint.class);
        server.start();
    }

    @AfterEach
    void destroy() throws Exception {
        server.stop();
    }

    @ParameterizedTest
    @ValueSource(strings = { "token1", "token2" })
    void 認証できる(String token) throws Exception {
        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=" + token))) {
            assertTrue(session.isOpen());
        }
    }

    @Test
    void 不正な認証トークン() throws Exception {
        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=unknowntoken"))) {
            assertEquals(CloseCodes.NORMAL_CLOSURE, client.getCloseReason().getCloseCode());
            assertEquals("Authentication failed", client.getCloseReason().getReasonPhrase());
            assertFalse(session.isOpen());
        }
    }

    @Test
    void 認証トークンが空文字列() throws Exception {
        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token="))) {
            assertEquals(CloseCodes.NORMAL_CLOSURE, client.getCloseReason().getCloseCode());
            assertEquals("Authentication failed", client.getCloseReason().getReasonPhrase());
            assertFalse(session.isOpen());
        }
    }

    @Test
    void 認証トークンがない() throws Exception {
        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification"))) {
            assertEquals(CloseCodes.NORMAL_CLOSURE, client.getCloseReason().getCloseCode());
            assertEquals("Authentication failed", client.getCloseReason().getReasonPhrase());
            assertFalse(session.isOpen());
        }
    }

    @Test
    void pingが来たらpongを返す() throws Exception {
        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=token1"))) {
            ObjectMapper om = new ObjectMapper();
            session.getBasicRemote().sendText(om.writeValueAsString(Map.of("type", "ping")));
            String message = client.dequeueMessage();
            assertNotNull(message);
            JsonNode pong = om.readTree(message);
            assertEquals("pong", pong.get("type").asText());
        }
    }

    @Test
    void ping以外のtypeへは応答しない() throws Exception {
        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=token1"))) {
            ObjectMapper om = new ObjectMapper();
            session.getBasicRemote().sendText(om.writeValueAsString(Map.of("type", "unknown")));
            String message = client.dequeueMessage();
            assertNull(message);
        }
    }

    @Test
    void ping以外のメッセージへは応答しない() throws Exception {
        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=token1"))) {
            session.getBasicRemote().sendText("unknown message");
            String message = client.dequeueMessage();
            assertNull(message);
        }
    }

    @ClientEndpoint
    public static class WebSocketClient {

        private final CountDownLatch closeLatch = new CountDownLatch(1);
        private final AtomicReference<CloseReason> closeReasonRef = new AtomicReference<>();
        private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        @OnMessage
        public void onMessage(String message) {
            queue.add(message);
        }

        @OnClose
        public void onClose(CloseReason closeReason) {
            closeReasonRef.set(closeReason);
            closeLatch.countDown();
        }

        public String dequeueMessage() throws InterruptedException {
            return queue.poll(TIMEOUT_VALUE, TIMEOUT_UNIT);
        }

        public CloseReason getCloseReason() throws InterruptedException {
            closeLatch.await(TIMEOUT_VALUE, TIMEOUT_UNIT);
            return closeReasonRef.get();
        }
    }

    static class MockAuthenticationTokenRepository implements AuthenticationTokenRepository {

        private Map<String, AccountId> value = new HashMap<>();

        MockAuthenticationTokenRepository add(String authenticationToken, String accountId) {
            value.put(authenticationToken, new AccountId(accountId));
            return this;
        }

        @Override
        public AccountId authenticate(AuthenticationToken authenticationToken) {
            return value.get(authenticationToken.value());
        }
    }
}
